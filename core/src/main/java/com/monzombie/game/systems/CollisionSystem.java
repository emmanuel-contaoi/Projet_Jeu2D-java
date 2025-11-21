package com.monzombie.game.systems;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.monzombie.game.Bullet;
import com.monzombie.game.Player;
import com.monzombie.game.Zombie;
import com.monzombie.game.entity.EtatVie;
import com.monzombie.game.util.Constants;

/**
 * Static helpers that resolve interactions between bullets, zombies and the player.
 */
public class CollisionSystem {

    private static boolean overlap(float ax,float ay,float aw,float ah,
                                   float bx,float by,float bw,float bh){
        boolean horizontal = ax < bx + bw && ax + aw > bx;
        boolean vertical   = ay < by + bh && ay + ah > by;
        return horizontal && vertical;
    }

    /**
     * Checks every bullet against every zombie and applies damage when they touch.
     *
     * @param bullets active bullets to test
     * @param zombies zombies currently alive
     * @return number of zombies killed during this pass
     */
    public static int bulletsVsZombies(Array<Bullet> bullets, Array<Zombie> zombies){
        int kills = 0;
        for (int zi = zombies.size - 1; zi >= 0; zi--) {
            Zombie z = zombies.get(zi);
            if (!z.estVivant()) continue;

            Rectangle zBox = z.getBounds();
            for (int bi = bullets.size - 1; bi >= 0; bi--) {
                Bullet b = bullets.get(bi);
                boolean hit = overlap(b.x, b.y, 10, 3, zBox.x, zBox.y, zBox.width, zBox.height);
                if (!hit) continue;

                EtatVie prevState = z.getEtatVie();
                z.subirDegats(50);
                bullets.removeIndex(bi);
                if (prevState == EtatVie.VIVANT && !z.estVivant()) {
                    kills++;
                }
                break; 
            }
        }
        return kills;
    }

    /**
     * Detects collisions between zombies and the player, inflicting damage and pushing them apart.
     *
     * @param zombies list of active zombies
     * @param player player instance
     * @param dt frame delta time used to cool down hit timers
     */
    public static void zombiesVsPlayer(Array<Zombie> zombies, Player player, float dt){
        Rectangle playerBox = player.getBounds();
        for (Zombie z : zombies) {
            if (!z.estVivant()) continue;

            Rectangle zBox = z.getBounds();
            boolean touchesPlayer = overlap(
                zBox.x, zBox.y, zBox.width, zBox.height,
                playerBox.x, playerBox.y, playerBox.width, playerBox.height
            );

            if (touchesPlayer) {
                if (z.hitCooldown <= 0f) {
                    z.hitCooldown = 0.8f;
                    player.subirDegats(Constants.ZOMBIE_DAMAGE);
                }
                separatePlayerAndZombie(z, playerBox, zBox);
            }

            if (z.hitCooldown > 0f) {
                z.hitCooldown -= dt;
                if (z.hitCooldown < 0f) z.hitCooldown = 0f;
            }
            playerBox = player.getBounds();
        }
    }

    private static void separatePlayerAndZombie(Zombie zombie,
                                                Rectangle playerBox, Rectangle zBox) {
        float overlapW = Math.min(playerBox.x + playerBox.width, zBox.x + zBox.width)
            - Math.max(playerBox.x, zBox.x);
        if (overlapW <= 0f) return;

        float push = overlapW + 1f;
        float playerCenter = playerBox.x + playerBox.width / 2f;
        float zombieCenter = zBox.x + zBox.width / 2f;
        if (playerCenter < zombieCenter) {
            zombie.nudgeHorizontally(push);
            if (zombie.vx < 0f) zombie.vx = 0f;
        } else {
            zombie.nudgeHorizontally(-push);
            if (zombie.vx > 0f) zombie.vx = 0f;
        }
    }
}
