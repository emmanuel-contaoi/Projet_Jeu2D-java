package com.monzombie.game.util;

/**
 * Regroupe les stats dynamiques des zombies selon le niveau.
 */
public class ZombieTuning {
    public final int hp;
    public final int damage;
    public final float minSpeed;
    public final float maxSpeed;
    public final float spawnMultiplier;
    public final int maxCount;

    public ZombieTuning(int hp,
                        int damage,
                        float minSpeed,
                        float maxSpeed,
                        float spawnMultiplier,
                        int maxCount) {
        this.hp = hp;
        this.damage = damage;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.spawnMultiplier = spawnMultiplier;
        this.maxCount = maxCount;
    }

    /**
     * Retourne les stats selon le niveau courant.
     */
    public static ZombieTuning forLevel(int levelNumber) {
        if (levelNumber >= 2) {
            int hp = Math.round(Constants.ZOMBIE_HP * 1.4f);
            int damage = Constants.ZOMBIE_DAMAGE + 1;
            float minSpeed = Constants.ZOMBIE_MIN_SPEED * 1.05f;
            float maxSpeed = Constants.ZOMBIE_MAX_SPEED * 1.05f;
            float spawnMultiplier = 1.25f;
            int maxCount = Math.round(Constants.ZOMBIE_MAX_COUNT * 1.2f);
            return new ZombieTuning(hp, damage, minSpeed, maxSpeed, spawnMultiplier, maxCount);
        }
        return new ZombieTuning(
            Constants.ZOMBIE_HP,
            Constants.ZOMBIE_DAMAGE,
            Constants.ZOMBIE_MIN_SPEED,
            Constants.ZOMBIE_MAX_SPEED,
            1f,
            Constants.ZOMBIE_MAX_COUNT
        );
    }
}
