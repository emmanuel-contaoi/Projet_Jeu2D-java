package com.monzombie.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.monzombie.game.util.Constants;

public class Zombie {
    public static final int ALIVE = 0;
    public static final int DYING = 1;

    public int state = ALIVE;

    public float x, y, w, h, speed;
    public int hp;

    public float hitCooldown = 0f;
    public boolean flipLeft  = false;
    public float animTime    = 0f;

    public float deathT = 0f;
    public float deathDur = 0.7f;
    public float vy = 0f;
    public float vr = 0f;
    public float rot = 0f;
    public float vx = 0f;

    private final Animation<TextureRegion> animWalk;
    private final Rectangle bounds = new Rectangle();
    private Rectangle patrolZone;
    private int zoneIndex = -1;
    private float patrolDir;

    public Zombie(float x, float y, float speed, Animation<TextureRegion> animWalk) {
        this.x = x;
        this.y = y;

        this.w = Constants.ZOMBIE_W;
        this.h = Constants.ZOMBIE_H;

        this.speed = speed;
        this.hp = Constants.ZOMBIE_HP;

        this.animWalk = animWalk;
        patrolDir = MathUtils.randomBoolean() ? 1f : -1f;
    }

    
    public void updateAlive(float dt, float playerCenterX) {
        float dir = selectDirection(playerCenterX);
        vx = dir * speed;
        x += vx * dt;
        clampInsideZone();
        animTime += dt;
        flipLeft = vx < 0;
    }

    private float selectDirection(float playerCenterX) {
        if (patrolZone == null) {
            float direction = Math.signum(playerCenterX - (x + w / 2f));
            return direction == 0 ? 1f : direction;
        }

        float zoneLeft = patrolZone.x;
        float zoneRight = patrolZone.x + patrolZone.width;
        boolean playerInside = playerCenterX >= zoneLeft && playerCenterX <= zoneRight;
        if (playerInside) {
            float direction = Math.signum(playerCenterX - (x + w / 2f));
            return direction == 0 ? patrolDir : direction;
        }

        float minX = zoneLeft;
        float maxX = zoneRight - w;
        if (maxX < minX) maxX = minX;

        if (x <= minX) {
            x = minX;
            patrolDir = 1f;
        } else if (x >= maxX) {
            x = maxX;
            patrolDir = -1f;
        }
        return patrolDir;
    }

    






    public boolean update(float dt, float targetX, Array<Rectangle> solids) {
        if (state == ALIVE) {
            updateAlive(dt, targetX);
            resolveSolids(solids);
            return false; 
        }
        
        return updateDying(dt, Constants.GROUND_H);
    }

    
    public void startDeath() {
        state = DYING;
        deathT = 0f;
        vy = MathUtils.random(600f, 900f);
        vr = MathUtils.random(-420f, 420f);
        rot = 0f;
    }

    



    public boolean updateDying(float dt, float ground) {
        deathT += dt;

        vy += Constants.GRAVITY * dt; 
        y  += vy * dt;

        rot += vr * dt; 

        if (y < ground) { 
            y = ground;
            vy *= -0.25f;
        }
        return deathT >= deathDur;
    }

    private void resolveSolids(Array<Rectangle> solids) {
        if (solids == null) return;
        Rectangle hitBox = getBounds();
        for (Rectangle solid : solids) {
            if (solid.y <= 0f && solid.height >= Constants.GROUND_H - 1f) {
                continue; 
            }
            if (!hitBox.overlaps(solid)) continue;

            if (vx > 0) {
                x = solid.x - w;
            } else if (vx < 0) {
                x = solid.x + solid.width;
            }
            vx = 0;
            hitBox = getBounds();
            clampInsideZone();
        }
    }

    private void clampInsideZone() {
        if (patrolZone == null) return;
        float min = patrolZone.x;
        float max = patrolZone.x + patrolZone.width - w;
        if (max < min) max = min;
        if (x < min) x = min;
        if (x > max) x = max;
    }

    
    public void nudgeHorizontally(float dx) {
        x += dx;
        clampInsideZone();
    }

    public void render(SpriteBatch b) {
        TextureRegion f = animWalk.getKeyFrame(animTime, true);
        if (flipLeft && !f.isFlipX()) f.flip(true, false);
        if (!flipLeft && f.isFlipX()) f.flip(true, false);
        b.draw(f, x, y, w, h);
    }

    public Rectangle getBounds() {
        float marginX = w * 0.2f;
        bounds.set(x + marginX, y, w - marginX * 2f, h);
        return bounds;
    }

    public void assignZone(Rectangle zone, int index) {
        this.patrolZone = zone;
        this.zoneIndex = zone != null ? index : -1;
        clampInsideZone();
    }

    public int getZoneIndex() {
        return zoneIndex;
    }
}
