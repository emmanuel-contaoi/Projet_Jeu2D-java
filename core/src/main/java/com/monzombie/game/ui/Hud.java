package com.monzombie.game.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Hud {
    private final TextureRegion heartFull, heartEmpty;
    private final Texture onePx;

    public Hud(TextureRegion heartFull, TextureRegion heartEmpty, Texture onePx){
        this.heartFull = heartFull; this.heartEmpty = heartEmpty; this.onePx = onePx;
    }

    public void render(SpriteBatch b, float cameraX, float viewportW, float viewportH, int health, int healthMax, int score){
        float uiX = cameraX - viewportW/2f + 16f;
        float uiY = viewportH - 16f - 36f;

        int heartsTotal = Math.max(1, healthMax / 10);
        int fullCount   = Math.max(0, Math.min(heartsTotal, (int)Math.floor(health / 10f)));

        float size = 32f, gap = 6f;
        for (int i=0;i<heartsTotal;i++){
            TextureRegion r = (i < fullCount) ? heartFull : heartEmpty;
            b.draw(r, uiX + i * (size + gap), uiY, size, size);
        }

        // petit score en texte "pixel" minimaliste
        float sx = uiX, sy = uiY - 18;
        drawText(b, "Score: "+score, sx, sy);
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
}
