package com.monzombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.monzombie.game.util.Constants;

public class Player {

    // Position & taille
    public float x, y;
    public float w, h;

    // Vitesse
    public float vx, vy;
    public boolean onGround = true;

    // Gameplay
    public int health = Constants.PLAYER_HP_MAX;
    public int score  = 0;

    // Orientation & tir
    private boolean facingLeft = false;
    private boolean shooting   = false;
    private Animation<TextureRegion> currentShot = null;
    private float animTime = 0f;

    // Animations
    private final Animation<TextureRegion> walk;
    private final Animation<TextureRegion> run;
    private final Animation<TextureRegion> shot;
    private final TextureRegion idle;

    private final Texture onePx;

    public Player(float x,
                  float ground,
                  Animation<TextureRegion> walk,
                  Animation<TextureRegion> run,
                  Animation<TextureRegion> shot,
                  TextureRegion idle,
                  Texture onePx) {

        this.x = x;
        this.y = ground;

        this.w = Constants.PLAYER_W;
        this.h = Constants.PLAYER_H;

        this.walk = walk;
        this.run  = run;
        this.shot = shot;
        this.idle = idle;

        this.onePx = onePx;
    }

    /**
     * Gère les entrées clavier / souris et éventuellement crée des balles.
     */
    public void updateInput(float dt, Array<Bullet> outBullets) {
        boolean moveLeft  = isLeftPressed();
        boolean moveRight = isRightPressed();
        boolean running   = isRunPressed();

        updateHorizontalSpeed(dt, moveLeft, moveRight, running);
        handleJump();
        handleShoot(outBullets);
    }

    /**
     * Physique simple : gravité, mouvement, collision avec les bords
     * et le sol (ground).
     */
    public void physics(float dt, float worldW, float ground) {
        vy += Constants.GRAVITY * dt;
        x  += vx * dt;
        y  += vy * dt;

        // Sol
        if (y <= ground) {
            y = ground;
            vy = 0;
            onGround = true;
        }

        // Limites du monde
        if (x < 0) x = 0;
        if (x > worldW - w) x = worldW - w;

        animTime += dt;
    }

    /**
     * Dessine le joueur avec la bonne animation selon vitesse / tir.
     */
    public void render(SpriteBatch b) {
        TextureRegion frame = selectFrame();
        faceFrame(frame);
        b.draw(frame, x, y, w, h);
    }

    /**
     * Rectangle utile pour les collisions (optionnel).
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }

    // ---------------------------------------------------------------------
    //  INPUT HELPERS
    // ---------------------------------------------------------------------

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
        float accel   = Constants.PLAYER_ACCEL;
        float maxWalk = Constants.PLAYER_WALK;
        float maxRun  = Constants.PLAYER_RUN;

        if (moveLeft)  { vx -= accel * dt; facingLeft = true; }
        if (moveRight) { vx += accel * dt; facingLeft = false; }

        // ralentit quand aucune touche n'est pressée
        if (!moveLeft && !moveRight) {
            vx *= (float)Math.pow(0.001f, dt);
        }

        float limit = running ? maxRun : maxWalk;
        if (vx >  limit) vx =  limit;
        if (vx < -limit) vx = -limit;
    }

    private void handleJump() {
        if (!onGround) return;
        if (!Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) return;
        vy = Constants.JUMP_IMPULSE;
        onGround = false;
    }

    private void handleShoot(Array<Bullet> outBullets) {
        boolean fire = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
            || Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
        if (shooting || !fire) return;

        shooting    = true;
        currentShot = shot;
        animTime    = 0f;

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

    private TextureRegion selectFrame() {
        if (shooting && currentShot != null) {
            TextureRegion frame = currentShot.getKeyFrame(animTime);
            if (currentShot.isAnimationFinished(animTime)) {
                shooting = false;
                currentShot = null;
                animTime = 0f;
            }
            return frame;
        }

        float absSpeed = Math.abs(vx);
        if (absSpeed > 360f) return run.getKeyFrame(animTime, true);
        if (absSpeed > 10f)  return walk.getKeyFrame(animTime, true);
        return idle;
    }

    private void faceFrame(TextureRegion frame) {
        boolean frameFlipped = frame.isFlipX();
        if (facingLeft && !frameFlipped) frame.flip(true, false);
        if (!facingLeft && frameFlipped) frame.flip(true, false);
    }
}
