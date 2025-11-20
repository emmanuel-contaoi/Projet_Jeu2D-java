package com.monzombie.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bullet {
    public float x,y,vx,vy,life;
    private final Texture onePx;

    public Bullet(float x,float y,float vx,float vy,float life, Texture onePx){
        this.x=x; this.y=y; this.vx=vx; this.vy=vy; this.life=life; this.onePx = onePx;
    }

    public boolean update(float dt){
        x += vx * dt; y += vy * dt; life -= dt;
        return life <= 0f;
    }

    public void render(SpriteBatch b){
        b.setColor(Color.ORANGE);
        b.draw(onePx, x, y, 10, 3);
        b.setColor(Color.WHITE);
    }
}
