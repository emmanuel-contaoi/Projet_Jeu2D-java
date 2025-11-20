package com.monzombie.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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

    private final Animation<TextureRegion> animWalk;

    public Zombie(float x, float y, float speed, Animation<TextureRegion> animWalk) {
        this.x = x;
        this.y = y;

        this.w = Constants.ZOMBIE_W;
        this.h = Constants.ZOMBIE_H;

        this.speed = speed;
        this.hp = Constants.ZOMBIE_HP;

        this.animWalk = animWalk;
    }

    /** Déplacement et animation quand le zombie est vivant. */
    public void updateAlive(float dt, float targetX) {
        float zombieCenter = x + w / 2f;
        float directionToPlayer = targetX - zombieCenter;
        float dir = Math.signum(directionToPlayer); // -1 ou 1

        flipLeft = dir < 0;
        x += dir * speed * dt;
        animTime += dt;
    }

    /**
     * Méthode utilitaire appelée par LevelScreen :
     * - si le zombie est ALIVE -> on le fait avancer vers le joueur
     * - s'il est DYING -> on joue son animation de mort
     *
     * @return true si le zombie a terminé sa mort et doit être supprimé.
     */
    public boolean update(float dt, float targetX) {
        if (state == ALIVE) {
            updateAlive(dt, targetX);
            return false; // encore vivant
        }
        // on utilise la hauteur de sol globale du jeu
        return updateDying(dt, Constants.GROUND_H);
    }

    /** Lance l'animation de mort (appelé quand il se fait toucher). */
    public void startDeath() {
        state = DYING;
        deathT = 0f;
        vy = MathUtils.random(600f, 900f);
        vr = MathUtils.random(-420f, 420f);
        rot = 0f;
    }

    /**
     * Met à jour l’animation de mort.
     * @return true si la mort est terminée et qu’on peut supprimer le zombie.
     */
    public boolean updateDying(float dt, float ground) {
        deathT += dt;

        vy += Constants.GRAVITY * dt; // chute
        y  += vy * dt;

        rot += vr * dt; // rotation

        if (y < ground) { // rebond
            y = ground;
            vy *= -0.25f;
        }
        return deathT >= deathDur;
    }

    public void render(SpriteBatch b) {
        TextureRegion f = animWalk.getKeyFrame(animTime, true);
        if (flipLeft && !f.isFlipX()) f.flip(true, false);
        if (!flipLeft && f.isFlipX()) f.flip(true, false);
        b.draw(f, x, y, w, h);
    }
}
