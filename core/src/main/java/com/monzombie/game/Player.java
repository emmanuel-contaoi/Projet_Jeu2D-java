package com.monzombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.monzombie.game.Zombie;
import com.monzombie.game.assets.HeroSpriteSet;
import com.monzombie.game.assets.HeroSpriteSet.Action;
import com.monzombie.game.util.Constants;
import com.monzombie.game.util.SettingsManager;

public class Player {

    
    public float x, y;
    public float w, h;

    
    public float vx, vy;
    public boolean onGround = true;

    
    public int health = Constants.PLAYER_HP_MAX;
    public int score = 0;

    
    private boolean facingLeft = false;

    private final Rectangle bounds = new Rectangle();
    private final Rectangle swordBounds = new Rectangle();
    private final HeroSpriteSet sprites;
    private final SettingsManager settings;
    private static final float SHOOT_ANIM_TIME = 0.25f;
    private static final int SWORD_DAMAGE = 100;
    private float shootAnimTimer = 0f;
    private float shootStateTime = 0f;
    private Action currentAction = Action.IDLE;
    private float actionTimer = 0f;

    public Player(float startX,
                  float groundY,
                  HeroSpriteSet sprites,
                  SettingsManager settings) {

        this.x = startX;
        this.y = groundY;

        this.w = Constants.PLAYER_W;
        this.h = Constants.PLAYER_H;

        this.sprites = sprites;
        this.settings = settings;
    }

    
    
    
    public void updateInput(float dt, Array<Zombie> zombies) {
        boolean moveLeft = isLeftPressed();
        boolean moveRight = isRightPressed();
        boolean running = isRunPressed();

        updateHorizontalSpeed(dt, moveLeft, moveRight, running);
        handleJump();
        handleSwordAttack(zombies);
        updateShootAnimation(dt);
        updateActionState(dt);
    }

    private boolean isLeftPressed() {
        int key = settings != null ? settings.getLeftKey() : Input.Keys.A;
        return Gdx.input.isKeyPressed(key) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    private boolean isRightPressed() {
        int key = settings != null ? settings.getRightKey() : Input.Keys.D;
        return Gdx.input.isKeyPressed(key) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
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
        int key = settings != null ? settings.getJumpKey() : Input.Keys.SPACE;
        if (!Gdx.input.isKeyJustPressed(key)) return;
        vy = Constants.JUMP_IMPULSE;
        onGround = false;
    }

    private void handleSwordAttack(Array<Zombie> zombies) {
        int attackKey = settings != null ? settings.getAttackKey() : Input.Keys.O;
        boolean swing = Gdx.input.isKeyJustPressed(attackKey);
        if (!swing) return;

        shootAnimTimer = SHOOT_ANIM_TIME;
        shootStateTime = 0f;
        currentAction = Action.SHOOT;
        actionTimer = 0f;

        if (zombies == null) return;

        Rectangle swordHit = swordHitBox();
        for (int i = 0; i < zombies.size; i++) {
            Zombie z = zombies.get(i);
            if (z.state != Zombie.ALIVE) continue;
            if (!swordHit.overlaps(z.getBounds())) continue;
            z.hp -= SWORD_DAMAGE;
            if (z.hp <= 0) {
                z.startDeath();
                score += 1;
            }
        }
    }

    private Rectangle swordHitBox() {
        float range = w * 0.9f;
        float height = h * 0.6f;
        float offsetX = facingLeft ? -range : w;
        float offsetY = h * 0.2f;
        swordBounds.set(x + offsetX, y + offsetY, range, height);
        return swordBounds;
    }

    private void updateShootAnimation(float dt) {
        if (shootAnimTimer <= 0f) return;
        shootAnimTimer -= dt;
        shootStateTime += dt;
        if (shootAnimTimer < 0f) {
            shootAnimTimer = 0f;
            shootStateTime = 0f;
        }
    }

    private void updateActionState(float dt) {
        Action desired = determineAction();
        if (desired != currentAction) {
            currentAction = desired;
            actionTimer = 0f;
        } else {
            actionTimer += dt;
        }
    }

    private Action determineAction() {
        if (shootAnimTimer > 0f) return Action.SHOOT;
        if (!onGround) return Action.JUMP;
        if (Math.abs(vx) > 20f) return Action.RUN;
        return Action.IDLE;
    }

    
    
    
    public void physics(float dt, float worldW, Array<Rectangle> solids) {
        
        vy += Constants.GRAVITY * dt;

        
        x += vx * dt;
        resolveHorizontal(solids, worldW);

        
        y += vy * dt;
        resolveVertical(solids);
    }

    private void resolveHorizontal(Array<Rectangle> solids, float worldW) {
        Rectangle hitBox = getBounds();
        for (Rectangle solid : solids) {
            if (!hitBox.overlaps(solid)) continue;
            if (vx > 0) {
                x = solid.x - w;
            } else if (vx < 0) {
                x = solid.x + solid.width;
            }
            vx = 0;
            hitBox = getBounds();
        }
        if (x < 0) x = 0;
        if (x > worldW - w) x = worldW - w;
    }

    private void resolveVertical(Array<Rectangle> solids) {
        onGround = false;
        Rectangle hitBox = getBounds();
        for (Rectangle solid : solids) {
            if (!hitBox.overlaps(solid)) continue;
            if (vy > 0) {
                y = solid.y - h;
                vy = 0;
            } else {
                y = solid.y + solid.height;
                vy = 0;
                onGround = true;
            }
            hitBox = getBounds();
        }
    }

    
    
    
    public void render(SpriteBatch b) {
        TextureRegion frame = selectFrame();
        if (frame != null) {
            b.draw(frame, x, y, w, h);
        }
    }

    private TextureRegion selectFrame() {
        if (sprites == null) return null;
        float stateTime = currentAction == Action.SHOOT ? shootStateTime : actionTimer;
        return sprites.frame(currentAction, facingLeft, stateTime);
    }

    
    
    
    public Rectangle getBounds() {
        float marginX = w * 0.2f;
        bounds.set(x + marginX, y, w - 2 * marginX, h);
        return bounds;
    }
}
