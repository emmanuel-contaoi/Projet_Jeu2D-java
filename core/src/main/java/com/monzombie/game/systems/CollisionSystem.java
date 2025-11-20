package com.monzombie.game.systems;

import com.badlogic.gdx.utils.Array;
import com.monzombie.game.Bullet;
import com.monzombie.game.Player;
import com.monzombie.game.Zombie;

public class CollisionSystem {

    private static boolean overlap(float ax,float ay,float aw,float ah,
                                   float bx,float by,float bw,float bh){
        boolean horizontal = ax < bx + bw && ax + aw > bx;
        boolean vertical   = ay < by + bh && ay + ah > by;
        return horizontal && vertical;
    }

    public static void bulletsVsZombies(Array<Bullet> bullets, Array<Zombie> zombies){
        for (int zi = zombies.size - 1; zi >= 0; zi--) {
            Zombie z = zombies.get(zi);
            if (z.state != Zombie.ALIVE) continue;

            for (int bi = bullets.size - 1; bi >= 0; bi--) {
                Bullet b = bullets.get(bi);
                boolean hit = overlap(b.x, b.y, 10, 3, z.x, z.y, z.w, z.h);
                if (!hit) continue;

                z.hp -= 50;
                bullets.removeIndex(bi);
                if (z.hp <= 0) z.startDeath();
                break; // on évite plusieurs balles sur le même zombie ce frame
            }
        }
    }

    public static void zombiesVsPlayer(Array<Zombie> zombies, Player player){
        for (Zombie z : zombies) {
            if (z.state != Zombie.ALIVE) continue;

            boolean touchesPlayer = overlap(
                z.x, z.y, z.w, z.h,
                player.x, player.y, player.w, player.h
            );

            if (touchesPlayer && z.hitCooldown <= 0f) {
                z.hitCooldown = 0.8f;
                player.health = Math.max(0, player.health - 10);
            }

            if (z.hitCooldown > 0f) z.hitCooldown -= 1 / 60f; // approx
        }
    }
}
