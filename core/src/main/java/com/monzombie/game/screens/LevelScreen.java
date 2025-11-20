package com.monzombie.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import TestForMain.MainGame;
import com.monzombie.game.Bullet;
import com.monzombie.game.Player;
import com.monzombie.game.Zombie;
import com.monzombie.game.assets.Assets;
import com.monzombie.game.systems.CollisionSystem;
import com.monzombie.game.systems.Spawner;
import com.monzombie.game.ui.Hud;
import com.monzombie.game.util.Constants;

public class LevelScreen implements Screen {

    private final MainGame game;
    private final int levelNumber;

    private final SpriteBatch batch;
    private final Viewport viewport;
    private final Assets assets;

    private Player player;
    private final Array<Bullet> bullets = new Array<>();
    private final Array<Zombie> zombies = new Array<>();
    private Spawner spawner;
    private Hud hud;

    private Texture onePx;

    private float worldWidth;
    private float cameraX;
    private boolean gameOver = false;

    public LevelScreen(MainGame game, int levelNumber) {
        this.game = game;
        this.levelNumber = levelNumber;
        this.assets = game.assets;
        this.batch = game.batch;
        this.viewport = new FitViewport(Constants.VW, Constants.VH);
        viewport.apply(true);
    }

    @Override
    public void show() {
        onePx = makeOnePx();

        // largeur du monde basée sur le fond bunker
        worldWidth = assets.computeWorldWidth(Constants.VH);

        // joueur posé sur le sol physique (GROUND_H)
        float startX = 300f;
        float groundY = Constants.GROUND_H;

        player = new Player(
            startX,
            groundY,
            assets.animWalk,
            assets.animRun,
            assets.animShot,
            assets.frameIdle,
            onePx
        );

        spawner = new Spawner();
        hud = new Hud(assets.heartFull, assets.heartEmpty, onePx);

        cameraX = startX + player.w / 2f;
    }

    @Override
    public void render(float delta) {
        float dt = clampDelta(delta);
        if (goBackToMenuIfNeeded()) return;

        handleInput(dt);
        updateGame(dt);
        updateCamera();
        drawFrame();
    }

    // ------------------------------------------------------------------------
    //  DESSIN FOND + SOL
    // ------------------------------------------------------------------------

    private void drawBackground() {
        if (assets.bg == null) return;
        float scale = Constants.VH / (float) assets.bg.getHeight();
        float w = assets.bg.getWidth() * scale;
        float h = Constants.VH;

        // on répète le bunker sur toute la largeur du monde
        float x = 0f;
        while (x < worldWidth) {
            batch.draw(assets.bg, x, 0, w, h);
            x += w;
        }
    }

    /**
     * Utilise map_1_bunker_sol.png comme sol :
     * - on scale la hauteur pour que le haut du sprite arrive à GROUND_H
     * - on répète la texture sur toute la largeur du monde
     */
    private void drawGround() {
        if (assets.groundTile == null) return;
        Texture t = assets.groundTile; // map_1_bunker_sol.png

        // échelle verticale : la texture occupe [0, GROUND_H]
        float scale = Constants.GROUND_H / (float) t.getHeight();
        float tileW = t.getWidth() * scale;
        float tileH = Constants.GROUND_H; // haut du tile = sol physique

        float x = 0f;
        while (x < worldWidth) {
            batch.draw(t, x, 0, tileW, tileH);
            x += tileW;
        }
    }

    // ------------------------------------------------------------------------
    //  UTILITAIRES / CYCLE ÉCRAN
    // ------------------------------------------------------------------------

    private float clampDelta(float delta) {
        return Math.min(delta, 1 / 30f);
    }

    private boolean goBackToMenuIfNeeded() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return true;
        }
        return false;
    }

    private void handleInput(float dt) {
        if (gameOver) return;
        player.updateInput(dt, bullets);
    }

    private void updateGame(float dt) {
        if (gameOver) return;

        player.physics(dt, worldWidth, Constants.GROUND_H);
        updateBullets(dt);
        updateZombies(dt);

        CollisionSystem.bulletsVsZombies(bullets, zombies);
        CollisionSystem.zombiesVsPlayer(zombies, player);
        checkPlayerDeath();
    }

    private void updateBullets(float dt) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            if (bullets.get(i).update(dt)) bullets.removeIndex(i);
        }
    }

    private void updateZombies(float dt) {
        spawner.update(dt, player.x, Constants.VW, zombies, assets.zombieWalk);
        for (int i = zombies.size - 1; i >= 0; i--) {
            if (zombies.get(i).update(dt, player.x)) zombies.removeIndex(i);
        }
    }

    private void checkPlayerDeath() {
        if (player.health > 0 || gameOver) return;
        gameOver = true;
        game.setScreen(new MenuScreen(game)); // simple reset: back to menu
    }

    private void updateCamera() {
        float halfW = Constants.VW / 2f;
        cameraX = player.x + player.w / 2f;
        cameraX = Math.max(halfW, Math.min(worldWidth - halfW, cameraX));
        viewport.getCamera().position.set(cameraX, Constants.VH / 2f, 0);
        viewport.getCamera().update();
    }

    private void drawFrame() {
        clearScreen();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        drawBackground();
        drawGround();

        player.render(batch);
        for (Bullet b : bullets) b.render(batch);
        for (Zombie z : zombies) z.render(batch);

        hud.render(batch, cameraX, Constants.VW, Constants.VH,
            player.health, Constants.PLAYER_HP_MAX, player.score);

        batch.end();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private Texture makeOnePx() {
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
        if (onePx != null) onePx.dispose();
    }

}
