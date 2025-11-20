package com.monzombie.game.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;






public class HeroSpriteSet {

    public enum Action {
        IDLE, RUN, JUMP, SHOOT
    }

    public static class DirectionalAnimation {
        private final Animation<TextureRegion> left;
        private final Animation<TextureRegion> right;

        public DirectionalAnimation(Animation<TextureRegion> left, Animation<TextureRegion> right) {
            this.left = left;
            this.right = right;
        }

        public Animation<TextureRegion> select(boolean facingLeft) {
            return facingLeft ? left : right;
        }
    }

    private final DirectionalAnimation idle;
    private final DirectionalAnimation run;
    private final DirectionalAnimation jump;
    private final DirectionalAnimation shoot;

    public HeroSpriteSet(DirectionalAnimation idle,
                         DirectionalAnimation run,
                         DirectionalAnimation jump,
                         DirectionalAnimation shoot) {
        this.idle = idle;
        this.run = run;
        this.jump = jump;
        this.shoot = shoot;
    }

    public TextureRegion frame(Action action, boolean facingLeft, float stateTime) {
        Animation<TextureRegion> animation = animationFor(action, facingLeft);
        if (animation == null) return null;
        boolean looping = action != Action.SHOOT;
        return animation.getKeyFrame(stateTime, looping);
    }

    private Animation<TextureRegion> animationFor(Action action, boolean facingLeft) {
        DirectionalAnimation directional;
        switch (action) {
            case RUN:
                directional = run;
                break;
            case JUMP:
                directional = jump;
                break;
            case SHOOT:
                directional = shoot;
                break;
            case IDLE:
            default:
                directional = idle;
        }
        return directional != null ? directional.select(facingLeft) : null;
    }
}
