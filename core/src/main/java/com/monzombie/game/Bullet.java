package com.monzombie.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Simplified projectile used for sword slashes and ranged attacks.
 */
public class Bullet {
    public float x,y,vx,vy,life;
    private final Texture onePx;

    /**
     * Creates a new projectile with an initial position, velocity and lifetime.
     *
     * @param x starting x coordinate
     * @param y starting y coordinate
     * @param vx horizontal velocity in units per second
     * @param vy vertical velocity in units per second
     * @param life duration before the bullet expires
     * @param onePx single pixel texture used for drawing
     */
    public Bullet(float x,float y,float vx,float vy,float life, Texture onePx){
        this.x=x; this.y=y; this.vx=vx; this.vy=vy; this.life=life; this.onePx = onePx;
    }

    /**
     * Advances the projectile for the current frame.
     *
     * @param dt delta time in seconds
     * @return true when the projectile lifetime hits zero
     */
    public boolean update(float dt){
        x += vx * dt; y += vy * dt; life -= dt;
        return life <= 0f;
    }

    /**
     * Draws the projectile along its travel direction.
     *
     * @param b sprite batch tied to the world camera
     */
    public void render(SpriteBatch b){
        b.setColor(Color.ORANGE);
        b.draw(onePx, x, y, 10, 3);
        b.setColor(Color.WHITE);
    }
}
