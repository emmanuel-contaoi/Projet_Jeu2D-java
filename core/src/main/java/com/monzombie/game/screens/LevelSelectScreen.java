package com.monzombie.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import TestForMain.MainGame;
import com.monzombie.game.util.Constants;

public class LevelSelectScreen implements Screen {

    private final MainGame game;
    private final SpriteBatch batch;
    private final Viewport viewport;

    // fond animé
    private Texture bg;
    private float bgOffsetX = 0f;
    private float bgSpeed = 20f;

    // dessin des boutons
    private Texture white1x1;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private final Rectangle rLvl1 = new Rectangle();
    private final Rectangle rLvl2 = new Rectangle();
    private final Rectangle rLvl3 = new Rectangle();
    private final Rectangle rBack = new Rectangle();

    private final Vector3 tmp = new Vector3();

    private boolean pressingL1, pressingL2, pressingL3, pressingBack;

    private static final float BTN_W = 360f;
    private static final float BTN_H = 90f;
    private static final float BTN_GAP = 28f;

    public LevelSelectScreen(MainGame game) {
        this.game = game;
        this.batch = game.batch;
        this.viewport = new FitViewport(Constants.VW, Constants.VH);
        viewport.apply(true);
    }

    @Override
    public void show() {
        font = new BitmapFont();
        font.getData().setScale(2f);

        white1x1 = makeWhite();

        // fond : priorité au menu_background, sinon bunker1
        if (Gdx.files.internal("menu_background.png").exists()) {
            bg = new Texture("menu_background.png");
        } else if (Gdx.files.internal("bunker1.jpg").exists()) {
            bg = new Texture("bunker1.jpg");
        }
        if (bg != null) {
            bg.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        // position des boutons au centre
        float x = (Constants.VW - BTN_W) / 2f;
        float top = Constants.VH / 2f + BTN_H + BTN_GAP;

        rLvl1.set(x, top, BTN_W, BTN_H);
        rLvl2.set(x, top - (BTN_H + BTN_GAP), BTN_W, BTN_H);
        rLvl3.set(x, top - 2 * (BTN_H + BTN_GAP), BTN_W, BTN_H);
        rBack.set(x, top - 3 * (BTN_H + BTN_GAP), BTN_W, BTN_H);
    }

    @Override
    public void render(float delta) {
        float dt = Math.min(delta, 1/30f);
        updateBackground(dt);

        Gdx.gl.glClearColor(0.06f, 0.07f, 0.09f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.unproject(tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        float mx = tmp.x, my = tmp.y;

        boolean justDown = Gdx.input.justTouched();
        boolean down = Gdx.input.isTouched();
        boolean justUp = !down && (pressingL1 || pressingL2 || pressingL3 || pressingBack);

        if (justDown) {
            pressingL1   = rLvl1.contains(mx, my);
            pressingL2   = rLvl2.contains(mx, my);
            pressingL3   = rLvl3.contains(mx, my);
            pressingBack = rBack.contains(mx, my);
        }

        if (justUp) {
            if (pressingL1 && rLvl1.contains(mx,my)) {
                // 👉 NOUVEAU : on passe par la sélection de personnages
                game.setScreen(new CharacterSelectScreen(game, 1));
                return;
            }
            if (pressingL2 && rLvl2.contains(mx,my)) {
                // tu peux plus tard mettre un CharacterSelectScreen(game, 2)
                System.out.println("Niveau 2 pas encore implémenté");
            }
            if (pressingL3 && rLvl3.contains(mx,my)) {
                // idem pour le niveau 3
                System.out.println("Niveau 3 pas encore implémenté");
            }
            if (pressingBack && rBack.contains(mx,my)) {
                game.setScreen(new MenuScreen(game));
                return;
            }
            pressingL1 = pressingL2 = pressingL3 = pressingBack = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        drawAnimatedBackground();

        drawButton(rLvl1, "NIVEAU 1", rLvl1.contains(mx,my), pressingL1);
        drawButton(rLvl2, "NIVEAU 2", rLvl2.contains(mx,my), pressingL2);
        drawButton(rLvl3, "NIVEAU 3", rLvl3.contains(mx,my), pressingL3);
        drawButton(rBack, "RETOUR",   rBack.contains(mx,my), pressingBack);

        batch.end();
    }

    // -------------------- fond animé --------------------

    private void updateBackground(float dt) {
        if (bg == null) return;
        bgOffsetX += bgSpeed * dt;
        if (bgOffsetX > bg.getWidth()) bgOffsetX -= bg.getWidth();
    }

    private void drawAnimatedBackground() {
        if (bg == null) return;

        float scale = Constants.VH / (float) bg.getHeight();
        float tileW = bg.getWidth() * scale;
        float x = -bgOffsetX * scale;

        batch.setColor(1f,1f,1f,0.85f);
        while (x < Constants.VW) {
            batch.draw(bg, x, 0, tileW, Constants.VH);
            x += tileW;
        }
        batch.setColor(Color.WHITE);
    }

    // -------------------- boutons --------------------

    private void drawButton(Rectangle r, String text,
                            boolean hover, boolean pressed) {

        Color base = new Color(0.16f,0.18f,0.22f,0.95f);
        Color hoverCol = new Color(0.24f,0.49f,0.86f,0.95f);
        Color fill = hover ? hoverCol : base;

        // ombre
        batch.setColor(0,0,0,0.35f);
        batch.draw(getWhite(), r.x+4, r.y-4, r.width, r.height);

        float s = pressed ? 0.96f : 1f;
        float w = r.width * s;
        float h = r.height * s;
        float x = r.x + (r.width - w)/2f;
        float y = r.y + (r.height - h)/2f;

        // fond
        batch.setColor(fill);
        batch.draw(getWhite(), x, y, w, h);

        // bord
        batch.setColor(0.92f,0.94f,0.98f,1f);
        float b = 4f;
        batch.draw(getWhite(), x, y, w, b);
        batch.draw(getWhite(), x, y+h-b, w, b);
        batch.draw(getWhite(), x, y, b, h);
        batch.draw(getWhite(), x+w-b, y, b, h);

        // texte
        batch.setColor(Color.WHITE);
        layout.setText(font, text);
        font.draw(batch, layout,
            x + (w - layout.width)/2f,
            y + (h + layout.height)/2f);
    }

    // -------------------- utilitaires / cycle --------------------

    private Texture makeWhite() {
        Pixmap pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pm.setColor(1,1,1,1);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

    private Texture getWhite() {
        if (white1x1 == null) white1x1 = makeWhite();
        return white1x1;
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (bg != null) bg.dispose();
        if (white1x1 != null) white1x1.dispose();
        if (font != null) font.dispose();
    }
}
