package com.monzombie.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * Loads, stores and applies gameplay settings and user preferences.
 */
public class SettingsManager {

    private static final String SAVE_FILE = "settings.json";
    private static final float MIN_BRIGHTNESS = 0.3f;
    private static final float MAX_BRIGHTNESS = 1f;

    /**
     * Serializable structure stored as JSON on disk.
     */
    public static class Data {
        public float brightness = 1f;
        public int keyLeft = Input.Keys.Q;
        public int keyRight = Input.Keys.D;
        public int keyJump = Input.Keys.SPACE;
        public int keyAttack = Input.Keys.O;
        public int resolutionWidth = 1280;
        public int resolutionHeight = 720;
    }

    private final Data data = new Data();
    private final Json json = new Json();

    /**
     * Creates a new manager and loads settings from disk.
     */
    public SettingsManager() {
        json.setOutputType(JsonWriter.OutputType.json);
        json.setIgnoreUnknownFields(true);
        load();
    }

    /**
     * Provides direct access to the mutable data object.
     *
     * @return settings data currently in memory
     */
    public Data getData() {
        return data;
    }

    /**
     * Returns the clamped brightness value for overlay drawing.
     *
     * @return brightness between {@link #MIN_BRIGHTNESS} and {@link #MAX_BRIGHTNESS}
     */
    public float getBrightness() {
        return MathUtils.clamp(data.brightness, MIN_BRIGHTNESS, MAX_BRIGHTNESS);
    }

    /**
     * Nudges the brightness and persists the new value.
     *
     * @param delta incremental change to apply
     */
    public void adjustBrightness(float delta) {
        data.brightness = MathUtils.clamp(data.brightness + delta, MIN_BRIGHTNESS, MAX_BRIGHTNESS);
        save();
    }

    /**
     * @return keyboard key bound to moving left.
     */
    public int getLeftKey() {
        return data.keyLeft;
    }

    /**
     * @return keyboard key bound to moving right.
     */
    public int getRightKey() {
        return data.keyRight;
    }

    /**
     * @return keyboard key bound to jumping.
     */
    public int getJumpKey() {
        return data.keyJump;
    }

    /**
     * @return keyboard key bound to melee attacks.
     */
    public int getAttackKey() {
        return data.keyAttack;
    }

    /**
     * Saves a new key binding for moving left.
     *
     * @param key LibGDX key code
     */
    public void setLeftKey(int key) {
        data.keyLeft = key;
        save();
    }

    /**
     * Saves a new key binding for moving right.
     *
     * @param key LibGDX key code
     */
    public void setRightKey(int key) {
        data.keyRight = key;
        save();
    }

    /**
     * Saves a new key binding for jumping.
     *
     * @param key LibGDX key code
     */
    public void setJumpKey(int key) {
        data.keyJump = key;
        save();
    }

    /**
     * Saves a new key binding for melee attacks.
     *
     * @param key LibGDX key code
     */
    public void setAttackKey(int key) {
        data.keyAttack = key;
        save();
    }

    /**
     * Stores a new resolution and applies it immediately.
     *
     * @param width window width in pixels
     * @param height window height in pixels
     */
    public void setResolution(int width, int height) {
        data.resolutionWidth = width;
        data.resolutionHeight = height;
        save();
        applyResolution();
    }

    /**
     * Applies the requested resolution in windowed mode when possible.
     */
    public void applyResolution() {
        if (Gdx.graphics == null) return;
        int w = Math.max(640, data.resolutionWidth);
        int h = Math.max(360, data.resolutionHeight);
        if (Gdx.graphics.getWidth() == w && Gdx.graphics.getHeight() == h) return;
        Gdx.graphics.setWindowedMode(w, h);
    }

    /**
     * Draws a translucent overlay to simulate lower brightness values.
     *
     * @param batch sprite batch currently rendering
     * @param white 1x1 white texture
     * @param startX x offset for drawing (useful in scrolling cameras)
     * @param width overlay width
     * @param height overlay height
     */
    public void drawBrightnessOverlay(SpriteBatch batch, Texture white, float startX, float width, float height) {
        if (batch == null || white == null) return;
        float b = getBrightness();
        if (b >= 0.999f) return;
        Color old = batch.getColor();
        batch.setColor(0f, 0f, 0f, 1f - b);
        batch.draw(white, startX, 0f, width, height);
        batch.setColor(old);
    }

    /**
     * Converts a LibGDX key code to a human-readable label.
     *
     * @param keyCode LibGDX key code
     * @return printable string
     */
    public String keyToString(int keyCode) {
        return Input.Keys.toString(keyCode);
    }

    private void load() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            if (!file.exists()) return;
            Data loaded = json.fromJson(Data.class, file.readString("UTF-8"));
            if (loaded != null) {
                data.brightness = loaded.brightness;
                data.keyLeft = loaded.keyLeft;
                data.keyRight = loaded.keyRight;
                data.keyJump = loaded.keyJump;
                data.keyAttack = loaded.keyAttack;
                data.resolutionWidth = loaded.resolutionWidth;
                data.resolutionHeight = loaded.resolutionHeight;
            }
        } catch (Exception ignored) {
        }
    }

    private void save() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            file.writeString(json.prettyPrint(data), false, "UTF-8");
        } catch (Exception ignored) {
        }
    }
}
