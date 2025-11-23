package com.monzombie.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.monzombie.game.entity.EtatVie;
import com.monzombie.game.entity.LivingEntity;
import com.monzombie.game.util.Constants;

public class Zombie extends LivingEntity {

    public float speed;
    private final int damage;

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
    private Rectangle patrolZone;
    private int zoneIndex = -1;
    private float patrolDir;

    /**
     * Creates a zombie with a spawn position, patrol speed and walk animation.
     *
     * @param x starting x coordinate
     * @param y ground aligned y coordinate
     * @param speed movement speed along patrol zones
     * @param animWalk animation played while the zombie is alive
     */
    public Zombie(float x, float y, float speed, int hp, int damage, Animation<TextureRegion> animWalk) {
        super(Constants.ZOMBIE_W, Constants.ZOMBIE_H, Math.max(1, hp));
        this.x = x;
        this.y = y;

        this.speed = speed;
        this.damage = Math.max(1, damage);

        this.animWalk = animWalk;
        patrolDir = MathUtils.randomBoolean() ? 1f : -1f;
    }

    
    /**
     * Moves an alive zombie either toward the player or within its patrol box.
     *
     * @param dt frame delta time
     * @param playerCenterX horizontal coordinate of the player center
     */
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

    






    /**
     * Updates the zombie state and resolves collisions with the level.
     *
     * @param dt frame delta time
     * @param targetX player target position guiding the chase
     * @param solids solid rectangles representing the level
     * @return true when the zombie finished its death animation
     */
    public boolean update(float dt, float targetX, Array<Rectangle> solids) {
        if (etatVie == EtatVie.VIVANT) {
            updateAlive(dt, targetX);
            resolveSolids(solids);
            return false; 
        }
        if (etatVie == EtatVie.AGONIE) {
            boolean done = updateDying(dt, Constants.GROUND_H);
            if (done) {
                changerEtat(EtatVie.MORT);
                return true;
            }
            return false;
        }
        return true;
    }

    
    /**
     * Triggers the rag-doll like falling animation and resets timers.
     */
    public void startDeath() {
        changerEtat(EtatVie.AGONIE);
        deathT = 0f;
        vy = MathUtils.random(600f, 900f);
        vr = MathUtils.random(-420f, 420f);
        rot = 0f;
    }

    



    /**
     * Applies gravity and rotation while the corpse bounces on the ground.
     *
     * @param dt frame delta time
     * @param ground y position of the ground to stop the fall
     * @return true when the corpse has been fading for long enough
     */
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

    
    /**
     * Pushes the zombie slightly to avoid stacking after a collision.
     *
     * @param dx horizontal displacement in world units
     */
    public void nudgeHorizontally(float dx) {
        x += dx;
        clampInsideZone();
    }

    /**
     * Draws the zombie with the appropriate orientation.
     *
     * @param b sprite batch tied to the world camera
     */
    public void render(SpriteBatch b) {
        TextureRegion f = animWalk.getKeyFrame(animTime, true);
        if (flipLeft && !f.isFlipX()) f.flip(true, false);
        if (!flipLeft && f.isFlipX()) f.flip(true, false);
        b.draw(f, x, y, w, h);
    }

    /**
     * Returns the bounding box used for collisions and attacks.
     *
     * @return cached rectangle representing the zombie body
     */
    @Override
    public Rectangle getBounds() {
        float marginX = w * 0.2f;
        bounds.set(x + marginX, y, w - marginX * 2f, h);
        return bounds;
    }

    /**
     * Assigns a patrol zone that constrains zombie movement.
     *
     * @param zone rectangle describing the zone, null for free roam
     * @param index index of the zone for bookkeeping
     */
    public void assignZone(Rectangle zone, int index) {
        this.patrolZone = zone;
        this.zoneIndex = zone != null ? index : -1;
        clampInsideZone();
    }

    public int getDamage() {
        return damage;
    }

    /**
     * Gives the index of the patrol zone currently assigned.
     *
     * @return zero-based zone index or -1 when not assigned
     */
    public int getZoneIndex() {
        return zoneIndex;
    }

    @Override
    protected void mourir() {
        if (etatVie == EtatVie.AGONIE || etatVie == EtatVie.MORT) return;
        startDeath();
    }
}
