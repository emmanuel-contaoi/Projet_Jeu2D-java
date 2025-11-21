package com.monzombie.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Locale;

/**
 * Lightweight HUD that draws hearts, elapsed time and kill statistics.
 */
public class Hud {
    private final Texture onePx;

    /**
     * Builds the HUD with the heart icons and a single pixel texture drawn as text.
     *
     * @param heartFull texture region used for remaining health
     * @param heartEmpty texture region used for missing health
     * @param onePx texture used to render placeholder text
     */
    public Hud(TextureRegion heartFull, TextureRegion heartEmpty, Texture onePx){
        this.onePx = onePx;
    }

    /**
     * Renders life hearts and simple text anchored to the screen corners.
     *
     * @param b sprite batch already set to the UI projection
     * @param cameraX horizontal center of the camera
     * @param viewportW viewport width in world units
     * @param viewportH viewport height in world units
     * @param health current health points
     * @param healthMax maximum health points
     * @param elapsedTime elapsed level time in seconds
     * @param killScore number of kills performed so far
     */
    public void render(SpriteBatch b, float cameraX, float viewportW, float viewportH,
                       int health, int healthMax, float elapsedTime, int killScore){
        float uiX = cameraX - viewportW/2f + 16f;
        float uiY = viewportH - 16f - 36f;

        drawHealthBar(b, uiX, uiY, health, healthMax);

        float sx = uiX;
        float sy = uiY - 28;
        drawText(b, "Temps: " + formatTime(elapsedTime), sx, sy);
        drawText(b, "Kills: " + killScore, sx, sy - 24);
    }

    private void drawText(SpriteBatch b, String txt, float x, float y){
        float cx = x;
        for (int i=0;i<txt.length();i++){
            char ch = txt.charAt(i);
            if (ch==' ') { cx += 8; continue; }
            b.draw(onePx, cx, y, 6, 12);
            cx += 8;
        }
    }

    private String formatTime(float elapsed) {
        int minutes = (int) (elapsed / 60f);
        float seconds = elapsed - minutes * 60f;
        return String.format(Locale.US, "%02d:%05.2f", minutes, seconds);
    }

    private void drawHealthBar(SpriteBatch b, float x, float y, int health, int healthMax) {
        if (onePx == null) return;
        float w = 260f;
        float h = 22f;
        int clampedHealth = Math.max(0, Math.min(healthMax, health));
        float ratio = Math.max(0f, Math.min(1f, clampedHealth / (float)Math.max(1, healthMax)));

        b.setColor(0f, 0f, 0f, 0.5f);
        b.draw(onePx, x, y, w, h);
        b.setColor(0.9f, 0.15f, 0.15f, 0.8f);
        b.draw(onePx, x + 3f, y + 3f, (w - 6f) * ratio, h - 6f);
        b.setColor(Color.WHITE);
        drawText(b, clampedHealth + "/" + healthMax, x + w + 12f, y + h / 2f - 6f);
    }
}
