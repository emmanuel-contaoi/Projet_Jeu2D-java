package com.monzombie.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import TestForMain.MainGame;
import com.monzombie.game.util.Constants;
import com.badlogic.gdx.files.FileHandle;

/**
 * Screen that lets the player pick between the available heroes before a level starts.
 */
public class CharacterSelectScreen implements Screen {

    private final MainGame game;
    private final int levelNumber;

    private final SpriteBatch batch;
    private final Viewport viewport;

    private Texture bg;
    private float bgOffsetX = 0f;
    private float bgSpeed = 20f;

    private Texture white1x1;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    
    private final Rectangle rChar1 = new Rectangle();
    private final Rectangle rChar2 = new Rectangle();
    private final Rectangle rBack  = new Rectangle();

    private final Vector3 tmp = new Vector3();

    private boolean pressingC1, pressingC2, pressingBack;

    private static final float CARD_W = 260f;
    private static final float CARD_H = 300f;
    private static final float CARD_GAP = 80f;
    private static final float BTN_W = 360f;
    private static final float BTN_H = 80f;

    
    private Texture texChar1, texChar2;
    private Animation<TextureRegion> animChar1, animChar2;
    private float animTime = 0f;
    private static final class PreviewOption {
        final String path;
        final int cols;
        final int rows;
        PreviewOption(String path, int cols, int rows) {
            this.path = path;
            this.cols = cols;
            this.rows = rows;
        }
    }

    private static final class PreviewData {
        final Texture texture;
        final Animation<TextureRegion> animation;
        PreviewData(Texture texture, Animation<TextureRegion> animation) {
            this.texture = texture;
            this.animation = animation;
        }
    }

    /**
     * Creates the selection screen for a specific level.
     *
     * @param game shared game instance
     * @param levelNumber level that will be loaded after choosing a hero
     */
    public CharacterSelectScreen(MainGame game, int levelNumber) {
        this.game = game;
        this.levelNumber = levelNumber;
        this.batch = game.batch;
        this.viewport = new FitViewport(Constants.VW, Constants.VH);
        viewport.apply(true);
    }

    /**
     * Allocates fonts, textures and cards when the screen becomes active.
     */
    @Override
    public void show() {
        font = new BitmapFont();
        font.getData().setScale(1.8f);

        white1x1 = makeWhite();

        
        bg = new Texture("menu_background.png");

        
        PreviewData alexisPreview = loadPreviewAnimation(
            new PreviewOption("sprit2.png", 4, 5),
            new PreviewOption("Sprite /player/alexis/alexiswalkwithwoodsword.png", 5, 1),
            new PreviewOption("Sprite /player/alexis/alexisjumpattacksword.png", 6, 1)
        );
        texChar1 = alexisPreview.texture;
        animChar1 = alexisPreview.animation;

        PreviewData hugoPreview = loadPreviewAnimation(
            new PreviewOption("spirit1.png", 4, 4),
            new PreviewOption("Sprite /player/hugo/hugojumpattacksword.png", 6, 1),
            new PreviewOption("Sprite /player/hugo/hugojumpgatling.png", 6, 1)
        );
        texChar2 = hugoPreview.texture;
        animChar2 = hugoPreview.animation;

        
        float totalW = CARD_W * 2 + CARD_GAP;
        float startX = (Constants.VW - totalW) / 2f;
        float centerY = Constants.VH / 2f + 40f;

        rChar1.set(startX,                 centerY - CARD_H / 2f, CARD_W, CARD_H);
        rChar2.set(startX + CARD_W + CARD_GAP, centerY - CARD_H / 2f, CARD_W, CARD_H);

        
        rBack.set((Constants.VW - BTN_W) / 2f, 50f, BTN_W, BTN_H);
    }

    /**
     * Handles button presses, updates animations and draws the selection UI.
     *
     * @param delta frame delta time
     */
    @Override
    public void render(float delta) {
        animTime += delta;

        Gdx.gl.glClearColor(0.06f, 0.07f, 0.09f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.unproject(tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        float mx = tmp.x, my = tmp.y;

        boolean justDown = Gdx.input.justTouched();
        boolean down = Gdx.input.isTouched();
        boolean justUp = !down && (pressingC1 || pressingC2 || pressingBack);

        if (justDown) {
            pressingC1 = rChar1.contains(mx, my);
            pressingC2 = rChar2.contains(mx, my);
            pressingBack = rBack.contains(mx, my);
        }

        if (justUp) {
            if (pressingC1 && rChar1.contains(mx, my)) {
                game.selectedHero = "Alexis";
                game.setScreen(new LevelScreen(game, levelNumber));
            }
            if (pressingC2 && rChar2.contains(mx, my)) {
                game.selectedHero = "Hugo";
                game.setScreen(new LevelScreen(game, levelNumber));
            }
            if (pressingBack && rBack.contains(mx, my)) game.setScreen(new LevelSelectScreen(game));

            pressingC1 = pressingC2 = pressingBack = false;
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        drawBackground();

        drawCharacterCard(rChar1, animChar1, "Alexis", rChar1.contains(mx,my), pressingC1);
        drawCharacterCard(rChar2, animChar2, "Hugo", rChar2.contains(mx,my), pressingC2);

        drawButton(rBack, "RETOUR", rBack.contains(mx,my), pressingBack);
        if (game.settings != null) {
            game.settings.drawBrightnessOverlay(batch, getWhite(), 0f, Constants.VW, Constants.VH);
        }

        batch.end();
    }

    

    private Animation<TextureRegion> buildSimpleAnimation(Texture tex, int cols, int rows, float fps) {
        TextureRegion[][] grid = TextureRegion.split(tex, tex.getWidth()/cols, tex.getHeight()/rows);
        Array<TextureRegion> frames = new Array<>();
        for (TextureRegion[] regions : grid) for (TextureRegion r : regions) frames.add(r);

        return new Animation<>(1f/fps, frames, Animation.PlayMode.LOOP);
    }

    private void drawBackground() {
        batch.draw(bg, 0, 0, Constants.VW, Constants.VH);
    }

    private void drawCharacterCard(Rectangle r, Animation<TextureRegion> anim, String name, boolean hover, boolean pressed) {
        Color fill = hover ? new Color(0.25f,0.48f,0.86f,0.95f) : new Color(0.16f,0.18f,0.22f,0.95f);

        batch.setColor(fill);
        batch.draw(getWhite(), r.x, r.y, r.width, r.height);

        
        batch.setColor(Color.WHITE);
        batch.draw(getWhite(), r.x, r.y, r.width, 4);
        batch.draw(getWhite(), r.x, r.y+r.height-4, r.width, 4);
        batch.draw(getWhite(), r.x, r.y, 4, r.height);
        batch.draw(getWhite(), r.x+r.width-4, r.y, 4, r.height);

        
        TextureRegion frame = anim.getKeyFrame(animTime);
        float imgH = r.height - 80f;
        float imgW = imgH * (frame.getRegionWidth() / (float) frame.getRegionHeight());
        float imgX = r.x + (r.width - imgW) / 2f;
        float imgY = r.y + 20f;

        batch.draw(frame, imgX, imgY, imgW, imgH);

        
        layout.setText(font, name);
        font.draw(batch, name, r.x + r.width/2f - layout.width/2f, r.y + r.height - 10f);
    }

    private void drawButton(Rectangle r, String label, boolean hover, boolean pressed) {
        Color fill = hover ? new Color(0.25f,0.48f,0.86f,0.95f) : new Color(0.16f,0.18f,0.22f,0.95f);

        batch.setColor(fill);
        batch.draw(getWhite(), r.x, r.y, r.width, r.height);

        batch.setColor(Color.WHITE);
        layout.setText(font, label);
        font.draw(batch, label, r.x + r.width/2f - layout.width/2f, r.y + r.height/2f + layout.height/2f);
    }

    private Texture makeWhite() {
        Pixmap pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pm.setColor(1,1,1,1);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

    private Texture getWhite() { return white1x1; }

    private PreviewData loadPreviewAnimation(PreviewOption... options) {
        if (options == null) throw new RuntimeException("Aucun sprite fourni");
        for (PreviewOption option : options) {
            if (option == null || option.path == null) continue;
            Texture texture = tryLoadTexture(option.path);
            if (texture == null) continue;
            Animation<TextureRegion> animation = buildSimpleAnimation(texture, option.cols, option.rows, 10f);
            return new PreviewData(texture, animation);
        }
        throw new RuntimeException("Sprites introuvables pour la selection heros");
    }

    private Texture tryLoadTexture(String path) {
        if (path == null || path.isEmpty()) return null;
        FileHandle fh = Gdx.files.internal(path);
        if (!fh.exists()) return null;
        Texture texture = new Texture(fh);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return texture;
    }

    /**
     * Keeps the UI centered when the window is resized.
     */
    @Override public void resize(int width, int height) { viewport.update(width,height,true); }

    /**
     * No-op but required by the {@link Screen} contract.
     */
    @Override public void pause() {}

    /**
     * No-op but required by the {@link Screen} contract.
     */
    @Override public void resume() {}

    /**
     * Invoked when leaving the screen; nothing to clean yet.
     */
    @Override public void hide() {}

    /**
     * Releases the temporary assets allocated by this screen.
     */
    @Override
    public void dispose() {
        if (bg != null) bg.dispose();
        if (white1x1 != null) white1x1.dispose();
        if (font != null) font.dispose();
        if (texChar1 != null) texChar1.dispose();
        if (texChar2 != null) texChar2.dispose();
    }
}
