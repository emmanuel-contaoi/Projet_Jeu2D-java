package com.monzombie.game.systems;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.monzombie.game.Zombie;
import com.monzombie.game.util.Constants;

public class Spawner {

    private Array<Rectangle> zones = new Array<>();
    private boolean populated = false;

    public void setZones(Array<Rectangle> zones) {
        this.zones = zones != null ? zones : new Array<Rectangle>();
        populated = false;
    }

    



    public void populateInitialZombies(Array<Zombie> zombies, Animation<TextureRegion> zWalk) {
        if (populated) return;
        if (zones == null || zones.size == 0) return;

        int totalSpawned = 0;
        int maxTotal = Constants.ZOMBIE_MAX_COUNT;
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
        return Math.max(2, (int)(zone.width / 420f));
    }

    private void spawnSet(Rectangle zone, int zoneIndex, int count, Array<Zombie> zombies, Animation<TextureRegion> zWalk) {
        if (zone == null) return;
        if (count <= 0) return;

        float spawnLeft = zone.x + 32f;
        float spawnRight = zone.x + zone.width - Constants.ZOMBIE_W - 32f;
        if (spawnRight < spawnLeft) spawnRight = spawnLeft;

        for (int i = 0; i < count; i++) {
            float spawnX = MathUtils.random(spawnLeft, spawnRight);
            float speed  = MathUtils.random(Constants.ZOMBIE_MIN_SPEED, Constants.ZOMBIE_MAX_SPEED);
            Zombie z = new Zombie(spawnX, Constants.GROUND_H, speed, zWalk);
            z.assignZone(zone, zoneIndex);
            zombies.add(z);
        }
    }

}
