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

public class SettingsManager {

    private static final String SAVE_FILE = "settings.json";
    private static final float MIN_BRIGHTNESS = 0.3f;
    private static final float MAX_BRIGHTNESS = 1f;

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

    public SettingsManager() {
        json.setOutputType(JsonWriter.OutputType.json);
        json.setIgnoreUnknownFields(true);
        load();
    }

    public Data getData() {
        return data;
    }

    public float getBrightness() {
        return MathUtils.clamp(data.brightness, MIN_BRIGHTNESS, MAX_BRIGHTNESS);
    }

    public void adjustBrightness(float delta) {
        data.brightness = MathUtils.clamp(data.brightness + delta, MIN_BRIGHTNESS, MAX_BRIGHTNESS);
        save();
    }

    public int getLeftKey() {
        return data.keyLeft;
    }

    public int getRightKey() {
        return data.keyRight;
    }

    public int getJumpKey() {
        return data.keyJump;
    }

    public int getAttackKey() {
        return data.keyAttack;
    }

    public void setLeftKey(int key) {
        data.keyLeft = key;
        save();
    }

    public void setRightKey(int key) {
        data.keyRight = key;
        save();
    }

    public void setJumpKey(int key) {
        data.keyJump = key;
        save();
    }

    public void setAttackKey(int key) {
        data.keyAttack = key;
        save();
    }

    public void setResolution(int width, int height) {
        data.resolutionWidth = width;
        data.resolutionHeight = height;
        save();
        applyResolution();
    }

    public void applyResolution() {
        if (Gdx.graphics == null) return;
        int w = Math.max(640, data.resolutionWidth);
        int h = Math.max(360, data.resolutionHeight);
        if (Gdx.graphics.getWidth() == w && Gdx.graphics.getHeight() == h) return;
        Gdx.graphics.setWindowedMode(w, h);
    }

    public void drawBrightnessOverlay(SpriteBatch batch, Texture white, float startX, float width, float height) {
        if (batch == null || white == null) return;
        float b = getBrightness();
        if (b >= 0.999f) return;
        Color old = batch.getColor();
        batch.setColor(0f, 0f, 0f, 1f - b);
        batch.draw(white, startX, 0f, width, height);
        batch.setColor(old);
    }

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
