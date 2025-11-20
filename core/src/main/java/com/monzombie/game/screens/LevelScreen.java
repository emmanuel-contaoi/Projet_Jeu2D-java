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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import TestForMain.MainGame;
import com.monzombie.game.Bullet;
import com.monzombie.game.Player;
import com.monzombie.game.Zombie;
import com.monzombie.game.assets.Assets;
import com.monzombie.game.assets.HeroSpriteSet;
import com.monzombie.game.systems.CollisionSystem;
import com.monzombie.game.systems.Spawner;
import com.monzombie.game.ui.Hud;
import com.monzombie.game.util.Constants;
import com.monzombie.game.world.LevelGeometry;

import java.util.Locale;

/**
 * Main gameplay screen that manages player movement, zombies and level objectives.
 */
public class LevelScreen implements Screen {

    private final MainGame game;
    private final int levelNumber;

    private final SpriteBatch batch;
    private final Viewport viewport;
    private final Assets assets;
    private final String chosenHero;

    private Player player;
    private final Array<Bullet> bullets = new Array<>();
    private final Array<Zombie> zombies = new Array<>();
    private Spawner spawner;
    private Hud hud;

    private Texture onePx;
    private float levelTimer = 0f;
    private BitmapFont timerFont;
    private final GlyphLayout timerLayout = new GlyphLayout();

    private float worldWidth;
    private float cameraX;
    private boolean gameOver = false;
    private boolean levelFinished = false;
    private final int levelGoalScore;
    private static final float DOOR_ZONE_WIDTH = 120f; 
    private LevelGeometry geometry;
    private Array<Rectangle> solidColliders;
    private Array<Rectangle> hazardZones;
    private Array<Rectangle> zombieZones;
    private boolean debugColliders = true;
    private boolean initialized = false;

    /**
     * Creates a level screen for the requested stage id.
     *
     * @param game core game instance
     * @param levelNumber 1-based level index used for goals and scores
     */
    public LevelScreen(MainGame game, int levelNumber) {
        this.game = game;
        this.levelNumber = levelNumber;
        this.levelGoalScore = Math.max(1, levelNumber * 5);
        this.assets = game.assets;
        this.batch = game.batch;
        this.viewport = new FitViewport(Constants.VW, Constants.VH);
        this.chosenHero = game.selectedHero != null ? game.selectedHero : "Hugo";
        viewport.apply(true);
    }

    /**
     * Loads the geometry, spawns the player and prepares HUD resources.
     */
    @Override
    public void show() {
        if (initialized) {
            viewport.apply(true);
            return;
        }
        levelTimer = 0f;
        onePx = makeOnePx();
        timerFont = new BitmapFont();
        timerFont.getData().setScale(2f);

        
        worldWidth = assets.computeWorldWidth(Constants.VH);
        geometry = new LevelGeometry(worldWidth);
        solidColliders = geometry.getSolids();
        hazardZones = geometry.getHazards();
        zombieZones = geometry.getZombieZones();

        
        float startX = 300f;
        float groundY = Constants.GROUND_H;

        HeroSpriteSet heroSprites = assets.getHeroSpriteSet(chosenHero);

        player = new Player(
            startX,
            groundY,
            heroSprites,
            game.settings
        );

        spawner = new Spawner();
        spawner.setZones(zombieZones);
        spawner.populateInitialZombies(zombies, assets.zombieWalk);
        hud = new Hud(assets.heartFull, assets.heartEmpty, onePx);

        cameraX = startX + player.w / 2f;
        initialized = true;
    }

    /**
     * Updates the simulation and renders the entire level.
     *
     * @param delta frame delta time
     */
    @Override
    public void render(float delta) {
        float dt = clampDelta(delta);
        if (!gameOver) {
            levelTimer += dt;
        }
        if (handlePauseRequest()) return;

        handleInput(dt);
        updateGame(dt);
        updateCamera();
        drawFrame();
    }

    
    
    

    private void drawBackground() {
        if (assets.bg == null) return;
        float h = Constants.VH;
        batch.draw(assets.bg, 0f, 0f, worldWidth, h);
    }

    




    private void drawGround() {
        if (assets.groundTile == null) return;
        Texture t = assets.groundTile; 

        
        float scale = Constants.GROUND_H / (float) t.getHeight();
        float tileW = t.getWidth() * scale;
        float tileH = Constants.GROUND_H; 

        float x = 0f;
        while (x < worldWidth) {
            batch.draw(t, x, 0, tileW, tileH);
            x += tileW;
        }
    }

    
    
    

    private float clampDelta(float delta) {
        return Math.min(delta, 1 / 30f);
    }

    private boolean handlePauseRequest() {
        if (!Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) return false;
        game.setScreen(new PauseScreen(game, this));
        return true;
    }

    private void handleInput(float dt) {
        if (gameOver) return;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugColliders = !debugColliders;
        }
        player.updateInput(dt, zombies);
    }

    private void updateGame(float dt) {
        if (gameOver) return;

        Array<Rectangle> solids = solidColliders != null ? solidColliders : new Array<>();
        player.physics(dt, worldWidth, solids);
        updateBullets(dt);
        updateZombies(dt);

        int kills = CollisionSystem.bulletsVsZombies(bullets, zombies);
        if (kills > 0) {
            player.score += kills;
        }
        CollisionSystem.zombiesVsPlayer(zombies, player, dt);
        checkHazards();
        if (Constants.GOD_MODE) {
            player.health = Constants.PLAYER_HP_MAX;
        }
        checkPlayerDeath();
        checkLevelCompletion();
    }

    private void updateBullets(float dt) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            if (bullets.get(i).update(dt)) bullets.removeIndex(i);
        }
    }

    private void updateZombies(float dt) {
        float playerCenter = player.x + player.w / 2f;
        for (int i = zombies.size - 1; i >= 0; i--) {
            Zombie z = zombies.get(i);
            if (z.update(dt, playerCenter, solidColliders)) {
                zombies.removeIndex(i);
            }
        }
    }

    private void checkPlayerDeath() {
        if (player.health > 0 || gameOver) return;
        gameOver = true;
        game.setScreen(new GameOverScreen(game, levelNumber));
    }

    private void checkHazards() {
        if (gameOver || player == null || player.health <= 0) return;
        if (player.y < -200f) {
            player.health = 0;
            return;
        }
        if (hazardZones == null) return;
        Rectangle hitBox = player.getBounds();
        for (Rectangle hazard : hazardZones) {
            if (!hitBox.overlaps(hazard)) continue;
            player.health = 0;
            return;
        }
    }

    private void checkLevelCompletion() {
        if (gameOver || levelFinished || player == null) return;
        if (player.score < levelGoalScore) return;
        if (!isOnFinishDoor()) return;

        levelFinished = true;
        gameOver = true;
        game.markLevelFinished(levelNumber);
        if (game.scoreManager != null) {
            game.scoreManager.addScore(levelNumber, levelTimer);
        }
        System.out.println("Niveau " + levelNumber + " terminé !");
        game.setScreen(new LevelSelectScreen(game));
    }

    private boolean isOnFinishDoor() {
        if (player == null) return false;
        float playerFront = player.x + player.w;
        float doorStart = Math.max(0f, worldWidth - DOOR_ZONE_WIDTH);
        return playerFront >= doorStart;
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
        if (geometry == null) {
            drawGround();
        }

        player.render(batch);
        for (Bullet b : bullets) b.render(batch);
        for (Zombie z : zombies) z.render(batch);

        if (debugColliders) {
            drawDebugColliders();
        }

        drawTimerDisplay();
        hud.render(batch, cameraX, Constants.VW, Constants.VH,
            player.health, Constants.PLAYER_HP_MAX, levelTimer, player.score);
        if (game.settings != null) {
            float left = cameraX - Constants.VW / 2f;
            game.settings.drawBrightnessOverlay(batch, onePx, left, Constants.VW, Constants.VH);
        }

        batch.end();
    }

    private void drawTimerDisplay() {
        if (timerFont == null) return;
        String text = "TEMPS " + formatTime(levelTimer);
        timerLayout.setText(timerFont, text);
        float x = cameraX - timerLayout.width / 2f;
        float y = Constants.VH - 40f;
        timerFont.setColor(Color.WHITE);
        timerFont.draw(batch, timerLayout, x, y);
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

    /**
     * Resizes the viewport while keeping the camera centered.
     */
    @Override public void resize(int width, int height) { viewport.update(width, height, true); }

    /**
     * No special processing is required when the game is paused.
     */
    @Override public void pause() {}

    /**
     * No special processing is required when the game resumes.
     */
    @Override public void resume() {}

    /**
     * Called when exiting the level screen; gameplay already stops elsewhere.
     */
    @Override public void hide() {}

    /**
     * Disposes temporary textures and fonts used only by this level instance.
     */
    @Override
    public void dispose() {
        if (onePx != null) onePx.dispose();
        if (timerFont != null) timerFont.dispose();
    }

    private void drawDebugColliders() {
        if (onePx == null) return;

        if (solidColliders != null) {
            Color fill = new Color(0f, 0.9f, 0.1f, 0.15f);
            Color border = new Color(0f, 0.8f, 0.2f, 0.8f);
            for (Rectangle r : solidColliders) {
                drawRect(r, fill, border);
            }
        }

        if (hazardZones != null) {
            Color fill = new Color(0.9f, 0.05f, 0.05f, 0.15f);
            Color border = new Color(1f, 0.15f, 0.15f, 0.9f);
            for (Rectangle r : hazardZones) {
                Rectangle vis = new Rectangle(r);
                if (vis.y < 0f) {
                    vis.height += vis.y;
                    vis.y = 0f;
                }
                if (vis.height < 0f) continue;
                drawRect(vis, fill, border);
            }
        }

        if (zombieZones != null) {
            Color fill = new Color(0.05f, 0.75f, 0.17f, 0.08f);
            Color border = new Color(0.15f, 0.95f, 0.25f, 0.7f);
            for (Rectangle r : zombieZones) {
                drawRect(r, fill, border);
            }
        }

        
        float doorStart = Math.max(0f, worldWidth - DOOR_ZONE_WIDTH);
        Rectangle doorRect = new Rectangle(doorStart, 0f, DOOR_ZONE_WIDTH, Constants.VH);
        drawRect(doorRect, new Color(0f, 0.4f, 1f, 0.08f), new Color(0f, 0.7f, 1f, 0.6f));

        batch.setColor(Color.WHITE);
    }

    private void drawRect(Rectangle r, Color fill, Color border) {
        if (onePx == null) return;
        batch.setColor(fill);
        batch.draw(onePx, r.x, r.y, r.width, r.height);

        float b = 2f;
        batch.setColor(border);
        batch.draw(onePx, r.x, r.y, r.width, b);
        batch.draw(onePx, r.x, r.y + r.height - b, r.width, b);
        batch.draw(onePx, r.x, r.y, b, r.height);
        batch.draw(onePx, r.x + r.width - b, r.y, b, r.height);
    }

    private String formatTime(float elapsed) {
        int minutes = (int) (elapsed / 60f);
        float seconds = elapsed - minutes * 60f;
        return String.format(Locale.US, "%02d:%05.2f", minutes, seconds);
    }
}
