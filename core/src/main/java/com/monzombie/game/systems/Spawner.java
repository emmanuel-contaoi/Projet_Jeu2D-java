package com.monzombie.game.systems;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.monzombie.game.Zombie;
import com.monzombie.game.util.Constants;
import com.monzombie.game.util.ZombieTuning;

/**
 * Creates zombie instances based on predefined patrol zones.
 */
public class Spawner {

    private Array<Rectangle> zones = new Array<>();
    private boolean populated = false;
    private int zombieHp = Constants.ZOMBIE_HP;
    private int zombieDamage = Constants.ZOMBIE_DAMAGE;
    private float minSpeed = Constants.ZOMBIE_MIN_SPEED;
    private float maxSpeed = Constants.ZOMBIE_MAX_SPEED;
    private float spawnMultiplier = 1f;
    private int maxZombieCount = Constants.ZOMBIE_MAX_COUNT;

    /**
     * Provides the spawn zones detected from the environment.
     *
     * @param zones list of rectangles used for patrol and spawn positions
     */
    public void setZones(Array<Rectangle> zones) {
        this.zones = zones != null ? zones : new Array<Rectangle>();
        populated = false;
    }

    /**
     * Modifie les stats de zombies suivant le niveau.
     */
    public void setTuning(ZombieTuning tuning) {
        if (tuning == null) {
            zombieHp = Constants.ZOMBIE_HP;
            zombieDamage = Constants.ZOMBIE_DAMAGE;
            minSpeed = Constants.ZOMBIE_MIN_SPEED;
            maxSpeed = Constants.ZOMBIE_MAX_SPEED;
            spawnMultiplier = 1f;
            maxZombieCount = Constants.ZOMBIE_MAX_COUNT;
            populated = false;
            return;
        }
        zombieHp = Math.max(1, tuning.hp);
        zombieDamage = Math.max(1, tuning.damage);
        minSpeed = Math.max(10f, tuning.minSpeed);
        maxSpeed = Math.max(minSpeed, tuning.maxSpeed);
        spawnMultiplier = Math.max(0.25f, tuning.spawnMultiplier);
        maxZombieCount = Math.max(1, tuning.maxCount);
        populated = false;
    }

    /**
     * Populates each zone with a handful of zombies until the global cap is reached.
     *
     * @param zombies destination array that receives the spawned zombies
     * @param zWalk animation shared by every zombie
     */
    public void populateInitialZombies(Array<Zombie> zombies, Animation<TextureRegion> zWalk) {
        if (populated) return;
        if (zones == null || zones.size == 0) return;

        int totalSpawned = 0;
        int maxTotal = maxZombieCount;
        for (int zoneIndex = 0; zoneIndex < zones.size; zoneIndex++) {
            if (totalSpawned >= maxTotal) break;
            Rectangle zone = zones.get(zoneIndex);
            int targetCount = Math.min(capacityForZone(zone), maxTotal - totalSpawned);
            spawnSet(zone, zoneIndex, targetCount, zombies, zWalk);
            totalSpawned += targetCount;
        }

        populated = true;
    }

    private int capacityForZone(Rectangle zone) {
        int base = Math.max(2, (int)(zone.width / 420f));
        return Math.max(2, Math.round(base * spawnMultiplier));
    }

    private void spawnSet(Rectangle zone, int zoneIndex, int count, Array<Zombie> zombies, Animation<TextureRegion> zWalk) {
        if (zone == null) return;
        if (count <= 0) return;

        float spawnLeft = zone.x + 32f;
        float spawnRight = zone.x + zone.width - Constants.ZOMBIE_W - 32f;
        if (spawnRight < spawnLeft) spawnRight = spawnLeft;

        for (int i = 0; i < count; i++) {
            float spawnX = MathUtils.random(spawnLeft, spawnRight);
            float speed  = MathUtils.random(minSpeed, maxSpeed);
            Zombie z = new Zombie(spawnX, Constants.GROUND_H, speed, zombieHp, zombieDamage, zWalk);
            z.assignZone(zone, zoneIndex);
            zombies.add(z);
        }
    }

}
