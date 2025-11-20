package com.monzombie.game.screens;

import TestForMain.MainGame;
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
import com.monzombie.game.util.Constants;

/**
 * Pause menu displayed over the game that lets the player resume, change settings or exit.
 */
public class PauseScreen implements Screen {

    private final MainGame game;
    private final LevelScreen pausedLevel;
    private final SpriteBatch batch;
    private final Viewport viewport;

    private Texture bg;
    private Texture white1x1;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private final Rectangle rContinue = new Rectangle();
    private final Rectangle rSettings = new Rectangle();
    private final Rectangle rExit = new Rectangle();
    private final Vector3 tmp = new Vector3();

    private boolean pressingContinue;
    private boolean pressingSettings;
    private boolean pressingExit;

    private static final float BTN_W = 360f;
    private static final float BTN_H = 90f;
    private static final float BTN_GAP = 28f;

    public PauseScreen(MainGame game, LevelScreen pausedLevel) {
        this.game = game;
        this.pausedLevel = pausedLevel;
        this.batch = game.batch;
        this.viewport = new FitViewport(Constants.VW, Constants.VH);
        viewport.apply(true);
    }

    @Override
    public void show() {
        font = new BitmapFont();
        font.getData().setScale(2f);
        white1x1 = makeWhite();

        if (Gdx.files.internal("menu_background.png").exists()) {
            bg = new Texture("menu_background.png");
        }

        float x = (Constants.VW - BTN_W) / 2f;
        float top = Constants.VH / 2f + BTN_H;
        rContinue.set(x, top, BTN_W, BTN_H);
        rSettings.set(x, top - (BTN_H + BTN_GAP), BTN_W, BTN_H);
        rExit.set(x, top - 2 * (BTN_H + BTN_GAP), BTN_W, BTN_H);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.06f, 0.07f, 0.09f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.unproject(tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0f));
        float mx = tmp.x;
        float my = tmp.y;
        boolean justDown = Gdx.input.justTouched();
        boolean down = Gdx.input.isTouched();
        boolean justUp = !down && (pressingContinue || pressingSettings || pressingExit);

        if (justDown) {
            pressingContinue = rContinue.contains(mx, my);
            pressingSettings = rSettings.contains(mx, my);
            pressingExit = rExit.contains(mx, my);
        }

        if (justUp) {
            if (pressingContinue && rContinue.contains(mx, my)) {
                resumeLevel();
                return;
            }
            if (pressingSettings && rSettings.contains(mx, my)) {
                game.setScreen(new SettingsScreen(game, this));
                return;
            }
            if (pressingExit && rExit.contains(mx, my)) {
                game.setScreen(new MenuScreen(game));
                return;
            }
            pressingContinue = pressingSettings = pressingExit = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            resumeLevel();
            return;
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        drawBackground();
        drawButton(rContinue, "CONTINUER", rContinue.contains(mx, my), pressingContinue);
        drawButton(rSettings, "SETTINGS", rSettings.contains(mx, my), pressingSettings);
        drawButton(rExit, "EXIT", rExit.contains(mx, my), pressingExit);
        if (game.settings != null) {
            game.settings.drawBrightnessOverlay(batch, white1x1, 0f, Constants.VW, Constants.VH);
        }

        batch.end();
    }

    private void drawBackground() {
        if (bg == null) return;
        batch.setColor(Color.WHITE);
        batch.draw(bg, 0f, 0f, Constants.VW, Constants.VH);
    }

    private void drawButton(Rectangle r, String text, boolean hover, boolean pressed) {
        Color fill = hover ? new Color(0.24f, 0.49f, 0.86f, 0.95f) : new Color(0.16f, 0.18f, 0.22f, 0.95f);
        batch.setColor(0f, 0f, 0f, 0.35f);
        batch.draw(white1x1, r.x + 4, r.y - 4, r.width, r.height);

        float scale = pressed ? 0.96f : 1f;
        float w = r.width * scale;
        float h = r.height * scale;
        float x = r.x + (r.width - w) / 2f;
        float y = r.y + (r.height - h) / 2f;

        batch.setColor(fill);
        batch.draw(white1x1, x, y, w, h);

        batch.setColor(Color.WHITE);
        layout.setText(font, text);
        font.draw(batch, layout,
            x + (w - layout.width) / 2f,
            y + (h + layout.height) / 2f);
    }

    private void resumeLevel() {
        game.setScreen(pausedLevel);
    }

    private Texture makeWhite() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
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
