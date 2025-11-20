package com.monzombie.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.monzombie.game.Zombie;
import com.monzombie.game.util.Constants;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Spawner {

    private float timer = 0f;
    private float interval = Constants.SPAWN_INTERVAL_START_MS / 1000f;

    public void update(float dt, float cameraX, float vw, Array<Zombie> zombies, Animation<TextureRegion> zWalk){
        timer -= dt;
        if (!readyToSpawn(zombies)) return;

        timer = interval;
        spawnWave(cameraX, vw, zombies, zWalk);
        speedUpSpawns();
    }

    private boolean readyToSpawn(Array<Zombie> zombies) {
        boolean tooManyZombies = zombies.size >= Constants.ZOMBIE_MAX_COUNT;
        boolean timerRunning   = timer > 0f;
        return !tooManyZombies && !timerRunning;
    }

    private void spawnWave(float cameraX, float vw, Array<Zombie> zombies, Animation<TextureRegion> zWalk) {
        int count = MathUtils.random(2, 4);
        for (int i = 0; i < count; i++) {
            float spawnX = cameraX + vw; // toujours à droite de la caméra
            float speed  = MathUtils.random(Constants.ZOMBIE_MIN_SPEED, Constants.ZOMBIE_MAX_SPEED);
            zombies.add(new Zombie(spawnX, Constants.GROUND_H, speed, zWalk));
        }
    }

    private void speedUpSpawns() {
        float min = Constants.SPAWN_INTERVAL_MIN_MS / 1000f;
        float step = Constants.SPAWN_INTERVAL_STEP_MS / 1000f;
        if (interval > min) {
            interval = Math.max(min, interval - step);
        }
    }
}
