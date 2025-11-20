package com.monzombie.game.util;

public final class Constants {
    private Constants() {}

    // viewport
    public static final float VW = 1280f;
    public static final float VH = 720f;

    // world
    public static final float GROUND_H = 120f;

    // player
    public static final int PLAYER_HP_MAX = 100;
    public static final float PLAYER_W = 150f;
    public static final float PLAYER_H = 150f;
    public static final float PLAYER_ACCEL = 1400f;
    public static final float PLAYER_WALK = 300f;
    public static final float PLAYER_RUN  = 560f;
    public static final float JUMP_IMPULSE = 900f;
    public static final float GRAVITY = -2000f;

    // bullets
    public static final float BULLET_SPEED = 950f;
    public static final float BULLET_LIFE  = 0.8f;

    // zombies
    public static final float ZOMBIE_W = 150f;
    public static final float ZOMBIE_H = 150f;
    public static final int   ZOMBIE_HP = 100;
    public static final float ZOMBIE_MIN_SPEED = 80f;
    public static final float ZOMBIE_MAX_SPEED = 120f;
    public static final int   ZOMBIE_MAX_COUNT = 45;

    // spawn
    public static final long SPAWN_INTERVAL_START_MS = 2500;
    public static final long SPAWN_INTERVAL_MIN_MS   = 1200;
    public static final long SPAWN_INTERVAL_STEP_MS  = 100;
}
