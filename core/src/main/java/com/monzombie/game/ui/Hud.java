package com.monzombie.game.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Locale;

public class Hud {
    private final TextureRegion heartFull, heartEmpty;
    private final Texture onePx;

    public Hud(TextureRegion heartFull, TextureRegion heartEmpty, Texture onePx){
        this.heartFull = heartFull; this.heartEmpty = heartEmpty; this.onePx = onePx;
    }

    public void render(SpriteBatch b, float cameraX, float viewportW, float viewportH,
                       int health, int healthMax, float elapsedTime, int killScore){
        float uiX = cameraX - viewportW/2f + 16f;
        float uiY = viewportH - 16f - 36f;

        boolean useDirectHearts = healthMax <= 20;
        int heartsTotal = useDirectHearts
            ? Math.max(1, healthMax)
            : Math.max(1, Math.round(healthMax / 10f));
        int fullCount = useDirectHearts
            ? Math.max(0, Math.min(heartsTotal, health))
            : Math.max(0, Math.min(heartsTotal, Math.round(health / 10f)));

        float size = 32f, gap = 6f;
        for (int i=0;i<heartsTotal;i++){
            TextureRegion r = (i < fullCount) ? heartFull : heartEmpty;
            b.draw(r, uiX + i * (size + gap), uiY, size, size);
        }

        
        float sx = uiX;
        float sy = uiY - 18;
        drawText(b, "Temps: " + formatTime(elapsedTime), sx, sy);
        drawText(b, "Kills: " + killScore, sx, sy - 20);
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
}
