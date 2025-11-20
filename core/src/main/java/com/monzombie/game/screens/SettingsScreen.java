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
import com.monzombie.game.util.SettingsManager;

/**
 * Screen dedicated to tweaking brightness, key bindings and resolution settings.
 */
public class SettingsScreen implements Screen {

    private final MainGame game;
    private final Screen previous;
    private final SpriteBatch batch;
    private final Viewport viewport;
    private Texture white1x1;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final Rectangle rBack = new Rectangle();
    private final Rectangle rBrightnessMinus = new Rectangle();
    private final Rectangle rBrightnessPlus = new Rectangle();
    private final Rectangle rKeyLeft = new Rectangle();
    private final Rectangle rKeyRight = new Rectangle();
    private final Rectangle rKeyJump = new Rectangle();
    private final Rectangle rKeyAttack = new Rectangle();
    private final Rectangle[] rResolutionButtons = new Rectangle[3];
    private final Vector3 tmp = new Vector3();
    private boolean pressingBack;
    private boolean pressingMinus;
    private boolean pressingPlus;
    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingJump;
    private boolean pressingAttack;
    private int pressingResolution = -1;
    private enum KeyTarget { LEFT, RIGHT, JUMP, ATTACK }
    private KeyTarget waitingTarget = null;
    private static final int[][] RES_OPTIONS = {
        {1280, 720},
        {1600, 900},
        {1920, 1080}
    };

    /**
     * Creates the settings screen that can return to the previous screen when done.
     *
     * @param game shared game instance
     * @param previous screen to restore when the player exits settings
     */
    public SettingsScreen(MainGame game, Screen previous) {
        this.game = game;
        this.previous = previous;
        this.batch = game.batch;
        this.viewport = new FitViewport(Constants.VW, Constants.VH);
        viewport.apply(true);
    }

    /**
     * Initializes fonts, UI rectangles and helper textures.
     */
    @Override
    public void show() {
        font = new BitmapFont();
        font.getData().setScale(1.8f);
        white1x1 = makeWhite();
        float btnW = 300f;
        float btnH = 70f;
        float centerX = Constants.VW / 2f;
        float startY = Constants.VH - 160f;
        rBrightnessMinus.set(centerX - 320f, startY, btnW, btnH);
        rBrightnessPlus.set(centerX + 20f, startY, btnW, btnH);
        float keyY = startY - 120f;
        rKeyLeft.set(centerX - 420f, keyY, 260f, btnH);
        rKeyRight.set(centerX - 130f, keyY, 260f, btnH);
        rKeyJump.set(centerX + 160f, keyY, 260f, btnH);
        rKeyAttack.set(centerX - 130f, keyY - 90f, 260f, btnH);
        rBack.set(centerX - btnW / 2f, 40f, btnW, btnH);
        float resY = keyY - 220f;
        for (int i = 0; i < rResolutionButtons.length; i++) {
            Rectangle r = new Rectangle(centerX - 420f + i * 280f, resY, 240f, btnH);
            rResolutionButtons[i] = r;
        }
    }

    /**
     * Handles button interaction, listens for remapped keys and draws the menu.
     *
     * @param delta frame delta time
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.06f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        listenForKeyBinding();
        viewport.unproject(tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        float mx = tmp.x;
        float my = tmp.y;
        boolean justDown = Gdx.input.justTouched();
        boolean down = Gdx.input.isTouched();
        boolean justUp = !down && (pressingBack || pressingMinus || pressingPlus || pressingLeft || pressingRight || pressingJump || pressingAttack || pressingResolution >= 0);
        if (justDown) {
            pressingBack = rBack.contains(mx, my);
            pressingMinus = rBrightnessMinus.contains(mx, my);
            pressingPlus = rBrightnessPlus.contains(mx, my);
            pressingLeft = rKeyLeft.contains(mx, my);
            pressingRight = rKeyRight.contains(mx, my);
            pressingJump = rKeyJump.contains(mx, my);
            pressingAttack = rKeyAttack.contains(mx, my);
            pressingResolution = -1;
            for (int i = 0; i < rResolutionButtons.length; i++) {
                if (rResolutionButtons[i].contains(mx, my)) {
                    pressingResolution = i;
                }
            }
        }
        if (justUp) {
            if (pressingMinus && rBrightnessMinus.contains(mx, my)) changeBrightness(-0.05f);
            if (pressingPlus && rBrightnessPlus.contains(mx, my)) changeBrightness(0.05f);
            if (pressingLeft && rKeyLeft.contains(mx, my)) waitingTarget = KeyTarget.LEFT;
            if (pressingRight && rKeyRight.contains(mx, my)) waitingTarget = KeyTarget.RIGHT;
            if (pressingJump && rKeyJump.contains(mx, my)) waitingTarget = KeyTarget.JUMP;
            if (pressingAttack && rKeyAttack.contains(mx, my)) waitingTarget = KeyTarget.ATTACK;
            if (pressingResolution >= 0 && rResolutionButtons[pressingResolution].contains(mx, my)) applyResolution(pressingResolution);
            if (pressingBack && rBack.contains(mx, my)) {
                Screen next = previous != null ? previous : new MenuScreen(game);
                game.setScreen(next);
                return;
            }
            pressingBack = pressingMinus = pressingPlus = pressingLeft = pressingRight = pressingJump = pressingAttack = false;
            pressingResolution = -1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Screen next = previous != null ? previous : new MenuScreen(game);
            game.setScreen(next);
            return;
        }
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        drawBackground();
        drawTexts();
        drawButtons(mx, my);
        if (game.settings != null) {
            game.settings.drawBrightnessOverlay(batch, white1x1, 0f, Constants.VW, Constants.VH);
        }
        batch.end();
    }

    private void changeBrightness(float delta) {
        if (game.settings == null) return;
        game.settings.adjustBrightness(delta);
    }

    private void applyResolution(int index) {
        if (game.settings == null) return;
        int[] opt = RES_OPTIONS[index];
        game.settings.setResolution(opt[0], opt[1]);
    }

    private void listenForKeyBinding() {
        if (waitingTarget == null || game.settings == null) return;
        for (int key = 0; key < Input.Keys.MAX_KEYCODE; key++) {
            if (!Gdx.input.isKeyJustPressed(key)) continue;
            if (waitingTarget == KeyTarget.LEFT) game.settings.setLeftKey(key);
            else if (waitingTarget == KeyTarget.RIGHT) game.settings.setRightKey(key);
            else if (waitingTarget == KeyTarget.JUMP) game.settings.setJumpKey(key);
            else if (waitingTarget == KeyTarget.ATTACK) game.settings.setAttackKey(key);
            waitingTarget = null;
            break;
        }
    }

    private void drawBackground() {
        batch.setColor(0.08f, 0.09f, 0.12f, 1f);
        batch.draw(white1x1, 0f, 0f, Constants.VW, Constants.VH);
        batch.setColor(Color.WHITE);
    }

    private void drawTexts() {
        font.setColor(Color.WHITE);
        layout.setText(font, "REGLAGES");
        font.draw(batch, layout, (Constants.VW - layout.width) / 2f, Constants.VH - 60f);
        if (waitingTarget != null) {
            String msg = "Appuie sur une touche pour " + waitingTarget.name();
            layout.setText(font, msg);
            font.draw(batch, msg, (Constants.VW - layout.width) / 2f, rKeyAttack.y - 60f);
        }
    }

    private void drawButtons(float mx, float my) {
        drawButton(rBrightnessMinus, "Luminosite -", rBrightnessMinus.contains(mx, my), pressingMinus);
        drawButton(rBrightnessPlus, "Luminosite +", rBrightnessPlus.contains(mx, my), pressingPlus);
        drawKeyButton(rKeyLeft, "Gauche", game.settings != null ? game.settings.keyToString(game.settings.getLeftKey()) : "-", rKeyLeft.contains(mx, my), pressingLeft);
        drawKeyButton(rKeyRight, "Droite", game.settings != null ? game.settings.keyToString(game.settings.getRightKey()) : "-", rKeyRight.contains(mx, my), pressingRight);
        drawKeyButton(rKeyJump, "Saut", game.settings != null ? game.settings.keyToString(game.settings.getJumpKey()) : "-", rKeyJump.contains(mx, my), pressingJump);
        drawKeyButton(rKeyAttack, "Attaque", game.settings != null ? game.settings.keyToString(game.settings.getAttackKey()) : "-", rKeyAttack.contains(mx, my), pressingAttack);
        for (int i = 0; i < rResolutionButtons.length; i++) {
            Rectangle r = rResolutionButtons[i];
            boolean hover = r.contains(mx, my);
            boolean pressed = pressingResolution == i;
            int[] opt = RES_OPTIONS[i];
            String label = opt[0] + "x" + opt[1];
            boolean selected = game.settings != null
                && game.settings.getData().resolutionWidth == opt[0]
                && game.settings.getData().resolutionHeight == opt[1];
            drawButton(r, selected ? "[ " + label + " ]" : label, hover, pressed);
        }
        drawButton(rBack, "RETOUR", rBack.contains(mx, my), pressingBack);
        if (game.settings != null) {
            String val = String.format("Luminosite: %.2f", game.settings.getBrightness());
            font.draw(batch, val, rBrightnessMinus.x, rBrightnessMinus.y + rBrightnessMinus.height + 30f);
        }
    }

    private void drawButton(Rectangle r, String text, boolean hover, boolean pressed) {
        Color fill = hover ? new Color(0.25f, 0.48f, 0.86f, 0.95f) : new Color(0.16f, 0.18f, 0.22f, 0.95f);
        float scale = pressed ? 0.97f : 1f;
        float w = r.width * scale;
        float h = r.height * scale;
        float x = r.x + (r.width - w) / 2f;
        float y = r.y + (r.height - h) / 2f;
        batch.setColor(0f, 0f, 0f, 0.35f);
        batch.draw(white1x1, x + 4f, y - 4f, w, h);
        batch.setColor(fill);
        batch.draw(white1x1, x, y, w, h);
        batch.setColor(Color.WHITE);
        layout.setText(font, text);
        font.draw(batch, text, x + (w - layout.width) / 2f, y + (h + layout.height) / 2f);
    }

    private void drawKeyButton(Rectangle r, String label, String key, boolean hover, boolean pressed) {
      drawButton(r, label + ": " + key, hover, pressed);
    }

    private Texture makeWhite() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

    /**
     * Keeps the viewport aligned with the new window size.
     */
    @Override public void resize(int width, int height) { viewport.update(width, height, true); }

    /**
     * Required by {@link Screen}; nothing specific needs to happen here.
     */
    @Override public void pause() {}

    /**
     * Required by {@link Screen}; nothing specific needs to happen here.
     */
    @Override public void resume() {}

    /**
     * Called when navigating away from the settings screen.
     */
    @Override public void hide() {}

    /**
     * Releases textures and fonts allocated by this screen.
     */
    @Override
    public void dispose() {
        if (white1x1 != null) white1x1.dispose();
        if (font != null) font.dispose();
    }
}
