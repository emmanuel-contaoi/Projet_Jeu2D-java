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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.monzombie.game.util.Constants;
import com.monzombie.game.util.ScoreManager;

import java.util.Locale;

/**
 * Displays the local leaderboard and allows the player to return to the menu.
 */

public class LeaderboardScreen implements Screen {

    private final MainGame game;
    private final SpriteBatch batch;
    private final Viewport viewport;

    private Texture bg;
    private Texture white1x1;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private final Rectangle rBack = new Rectangle();
    private final Vector3 tmp = new Vector3();
    private boolean pressingBack = false;

    /**
     * Builds the leaderboard screen using the shared SpriteBatch.
     *
     * @param game game instance exposing managers and settings
     */
    public LeaderboardScreen(MainGame game) {
        this.game = game;
        this.batch = game.batch;
        this.viewport = new FitViewport(Constants.VW, Constants.VH);
        viewport.apply(true);
    }

    /**
     * Loads the background, button texture and positions widgets.
     */
    @Override
    public void show() {
        font = new BitmapFont();
        font.getData().setScale(2f);
        white1x1 = makeWhite();

        if (Gdx.files.internal("menu_background.png").exists()) {
            bg = new Texture("menu_background.png");
        } else if (Gdx.files.internal("bunker.jpg").exists()) {
            bg = new Texture("bunker.jpg");
        }

        float btnW = 360f;
        float btnH = 80f;
        rBack.set((Constants.VW - btnW) / 2f, 60f, btnW, btnH);
    }

    /**
     * Handles button interactions and draws the leaderboard entries.
     *
     * @param delta frame delta time
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.06f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }

        viewport.unproject(tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        float mx = tmp.x, my = tmp.y;
        boolean justDown = Gdx.input.justTouched();
        boolean down = Gdx.input.isTouched();
        boolean justUp = !down && pressingBack;

        if (justDown) {
            pressingBack = rBack.contains(mx, my);
        }

        if (justUp) {
            if (pressingBack && rBack.contains(mx, my)) {
                game.setScreen(new MenuScreen(game));
                return;
            }
            pressingBack = false;
        }

        Array<ScoreManager.ScoreEntry> topScores = (game.scoreManager != null)
            ? game.scoreManager.getTopScores(10)
            : new Array<>();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        drawBackground();
        drawTitle();
        drawScores(topScores);
        drawButton(rBack, "RETOUR", rBack.contains(mx, my), pressingBack);
        if (game.settings != null) {
            game.settings.drawBrightnessOverlay(batch, getWhite(), 0f, Constants.VW, Constants.VH);
        }

        batch.end();
    }

    private void drawBackground() {
        if (bg == null) return;
        batch.setColor(1f,1f,1f,0.9f);
        batch.draw(bg, 0f, 0f, Constants.VW, Constants.VH);
        batch.setColor(Color.WHITE);
    }

    private void drawTitle() {
        String title = "LEADERBOARD (TOP 10)";
        layout.setText(font, title);
        font.draw(batch, title,
            (Constants.VW - layout.width) / 2f,
            Constants.VH - 80f);
    }

    private void drawScores(Array<ScoreManager.ScoreEntry> scores) {
        float startY = Constants.VH - 140f;
        float lineHeight = 36f;
        if (scores.size == 0) {
            String msg = "Aucun temps enregistré pour l'instant";
            layout.setText(font, msg);
            font.draw(batch, msg,
                (Constants.VW - layout.width) / 2f,
                startY);
            return;
        }

        for (int i = 0; i < scores.size; i++) {
            ScoreManager.ScoreEntry entry = scores.get(i);
            String playerName = entry.playerName != null ? entry.playerName : "Anonyme";
            String line = String.format(Locale.US, "%2d. %s - Niveau %d - %s",
                i + 1, playerName, entry.level, formatTime(entry.timeSeconds));
            layout.setText(font, line);
            font.draw(batch, line,
                (Constants.VW - layout.width) / 2f,
                startY - i * lineHeight);
        }
    }

    private String formatTime(float timeSeconds) {
        int minutes = (int) (timeSeconds / 60f);
        float seconds = timeSeconds - minutes * 60f;
        return String.format(Locale.US, "%02d:%05.2f", minutes, seconds);
    }

    private void drawButton(Rectangle r, String label, boolean hover, boolean pressed) {
        Color fill = hover ? new Color(0.24f,0.49f,0.86f,0.95f) : new Color(0.16f,0.18f,0.22f,0.95f);

        batch.setColor(0f,0f,0f,0.35f);
        batch.draw(getWhite(), r.x + 4, r.y - 4, r.width, r.height);

        float s = pressed ? 0.96f : 1f;
        float w = r.width * s;
        float h = r.height * s;
        float x = r.x + (r.width - w)/2f;
        float y = r.y + (r.height - h)/2f;

        batch.setColor(fill);
        batch.draw(getWhite(), x, y, w, h);

        batch.setColor(Color.WHITE);
        float b = 4f;
        batch.draw(getWhite(), x, y, w, b);
        batch.draw(getWhite(), x, y + h - b, w, b);
        batch.draw(getWhite(), x, y, b, h);
        batch.draw(getWhite(), x + w - b, y, b, h);

        layout.setText(font, label);
        font.draw(batch, label,
            x + (w - layout.width)/2f,
            y + (h + layout.height)/2f);
    }

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

    /**
     * Keeps the viewport aligned with window changes.
     */
    @Override public void resize(int width, int height) { viewport.update(width, height, true); }

    /**
     * Required by {@link Screen}; no persistent work is needed here.
     */
    @Override public void pause() {}

    /**
     * Required by {@link Screen}; no persistent work is needed here.
     */
    @Override public void resume() {}

    /**
     * Called when leaving the screen; nothing to do at the moment.
     */
    @Override public void hide() {}

    /**
     * Disposes the textures and fonts allocated for the leaderboard.
     */
    @Override
    public void dispose() {
        if (bg != null) bg.dispose();
        if (white1x1 != null) white1x1.dispose();
        if (font != null) font.dispose();
    }
}
