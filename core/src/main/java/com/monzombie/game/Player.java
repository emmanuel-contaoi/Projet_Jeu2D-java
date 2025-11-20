package com.monzombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.monzombie.game.util.Constants;

public class Player {

    // Position et taille
    public float x, y;
    public float w, h;

    // Vitesse
    public float vx, vy;
    public boolean onGround = true;

    // Stats
    public int health = Constants.PLAYER_HP_MAX;
    public int score = 0;

    // Orientation
    private boolean facingLeft = false;

    // listes de frames simples
    private final Array<TextureRegion> groundLeftFrames;
    private final Array<TextureRegion> groundRightFrames;
    private final Array<TextureRegion> jumpLeftFrames;
    private final Array<TextureRegion> jumpRightFrames;

    private float animTime = 0f;

    private final Texture onePx;

    public Player(float startX,
                  float groundY,
                  Array<TextureRegion> groundLeftFrames,
                  Array<TextureRegion> groundRightFrames,
                  Array<TextureRegion> jumpLeftFrames,
                  Array<TextureRegion> jumpRightFrames,
                  Texture onePx) {

        this.x = startX;
        this.y = groundY;

        this.w = Constants.PLAYER_W;
        this.h = Constants.PLAYER_H;

        this.groundLeftFrames = groundLeftFrames;
        this.groundRightFrames = groundRightFrames;
        this.jumpLeftFrames = jumpLeftFrames;
        this.jumpRightFrames = jumpRightFrames;

        this.onePx = onePx;
    }

    // ------------------------------------------------------
    // Entrées clavier / souris
    // ------------------------------------------------------
    public void updateInput(float dt, Array<Bullet> outBullets) {
        boolean moveLeft = isLeftPressed();
        boolean moveRight = isRightPressed();
        boolean running = isRunPressed();

        updateHorizontalSpeed(dt, moveLeft, moveRight, running);
        handleJump();
        handleShoot(outBullets);
    }

    private boolean isLeftPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    private boolean isRightPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }

    private boolean isRunPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
    }

    private void updateHorizontalSpeed(float dt, boolean moveLeft, boolean moveRight, boolean running) {
        float accel = Constants.PLAYER_ACCEL;
        float maxWalk = Constants.PLAYER_WALK;
        float maxRun = Constants.PLAYER_RUN;

        if (moveLeft) {
            vx -= accel * dt;
            facingLeft = true;
        }
        if (moveRight) {
            vx += accel * dt;
            facingLeft = false;
        }

        if (!moveLeft && !moveRight) {
            vx *= (float) Math.pow(0.001f, dt);
        }

        float limit = running ? maxRun : maxWalk;
        if (vx > limit) vx = limit;
        if (vx < -limit) vx = -limit;
    }

    private void handleJump() {
        if (!onGround) return;
        if (!Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) return;
        vy = Constants.JUMP_IMPULSE;
        onGround = false;
    }

    private void handleShoot(Array<Bullet> outBullets) {
        boolean fire = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
        if (!fire) return;

        float dir = facingLeft ? -1f : 1f;
        float startX = facingLeft ? x : x + w;
        float startY = y + h * 0.65f;

        outBullets.add(new Bullet(
            startX,
            startY,
            dir * Constants.BULLET_SPEED,
            0,
            Constants.BULLET_LIFE,
            onePx
        ));
    }

    // ------------------------------------------------------
    // Physique simple
    // ------------------------------------------------------
    public void physics(float dt, float worldW, float ground) {
        vy += Constants.GRAVITY * dt;
        x += vx * dt;
        y += vy * dt;
        animTime += dt;

        if (y <= ground) {
            y = ground;
            vy = 0;
            onGround = true;
        }

        if (x < 0) x = 0;
        if (x > worldW - w) x = worldW - w;
    }

    // ------------------------------------------------------
    // Affichage
    // ------------------------------------------------------
    public void render(SpriteBatch b) {
        TextureRegion frame = selectFrame();
        if (frame != null) {
            b.draw(frame, x, y, w, h);
        }
    }

    private TextureRegion selectFrame() {
        if (!onGround) {
            if (facingLeft) return pickFrame(jumpLeftFrames);
            return pickFrame(jumpRightFrames);
        }

        if (facingLeft) return pickFrame(groundLeftFrames);
        return pickFrame(groundRightFrames);
    }

    // line of code

    private TextureRegion pickFrame(Array<TextureRegion> frames) {
        if (frames == null || frames.size == 0) return null;
        int fps = 6; // simple animation lente
        int index = (int)(animTime * fps) % frames.size;
        return frames.get(index);
    }

    // ------------------------------------------------------
    // Collisions utilitaire
    // ------------------------------------------------------
    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }
}
