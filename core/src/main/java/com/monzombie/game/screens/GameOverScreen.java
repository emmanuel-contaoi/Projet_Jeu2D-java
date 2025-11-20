package com.monzombie.game.screens;

import TestForMain.MainGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.monzombie.game.util.Constants;







public class GameOverScreen implements Screen {

    private static final float DISPLAY_TIME = 2f;

    private final MainGame game;
    private final SpriteBatch batch;
    private final int lastLevel;
    private final Viewport viewport;

    private Texture background;
    private Texture overlay1x1;
    private float timer = 0f;
    private boolean returning = false;

    public GameOverScreen(MainGame game, int lastLevel) {
        this.game = game;
        this.batch = game.batch;
        this.lastLevel = lastLevel;
        this.viewport = new FitViewport(Constants.VW, Constants.VH);
        viewport.apply(true);
    }

    @Override
    public void show() {
        
        background = loadTextureIfExists("gameoverscreen.jpg");
        if (background == null) {
            background = loadTextureIfExists("gameoverscreen.png");
        }
        if (background == null) {
            background = loadTextureIfExists("menu_background.png");
        }
        overlay1x1 = makeWhite();
    }

    @Override
    public void render(float delta) {
        timer += delta;
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleSkipInput();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        drawBackground();
        if (game.settings != null) {
            game.settings.drawBrightnessOverlay(batch, overlay1x1, 0f, Constants.VW, Constants.VH);
        }
        batch.end();

        if (!returning && timer >= DISPLAY_TIME) {
            goBackToMenu();
        }
    }

    private void drawBackground() {
        if (background != null) {
            batch.draw(background, 0f, 0f, Constants.VW, Constants.VH);
        }
    }

    private void handleSkipInput() {
        if (returning) return;
        if (Gdx.input.justTouched()
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            goBackToMenu();
        }
    }

    private void goBackToMenu() {
        if (returning) return;
        returning = true;
        game.setScreen(new MenuScreen(game));
    }

    private Texture loadTextureIfExists(String path) {
        try {
            if (!Gdx.files.internal(path).exists()) return null;
            Texture t = new Texture(Gdx.files.internal(path));
            t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            return t;
        } catch (Exception ex) {
            Gdx.app.error("GameOverScreen", "Impossible de charger " + path, ex);
            return null;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (background != null) background.dispose();
        if (overlay1x1 != null) overlay1x1.dispose();
    }

    private Texture makeWhite() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }
}
