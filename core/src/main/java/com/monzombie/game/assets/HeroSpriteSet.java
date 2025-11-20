package com.monzombie.game.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Groups directional animations for each player action to simplify rendering.
 */
public class HeroSpriteSet {

    /**
     * Lists the possible animation states for a hero.
     */
    public enum Action {
        IDLE, RUN, JUMP, SHOOT
    }

    /**
     * Holds the left and right counterparts of the same animation.
     */
    public static class DirectionalAnimation {
        private final Animation<TextureRegion> left;
        private final Animation<TextureRegion> right;

        /**
         * Creates a directional animation using two mirrored Animation instances.
         *
         * @param left animation to use while facing left
         * @param right animation to use while facing right
         */
        public DirectionalAnimation(Animation<TextureRegion> left, Animation<TextureRegion> right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Returns the animation matching the current facing direction.
         *
         * @param facingLeft true when the hero is looking left
         * @return selected Animation or null if missing
         */
        public Animation<TextureRegion> select(boolean facingLeft) {
            return facingLeft ? left : right;
        }
    }

    private final DirectionalAnimation idle;
    private final DirectionalAnimation run;
    private final DirectionalAnimation jump;
    private final DirectionalAnimation shoot;

    /**
     * Builds a sprite set containing the four hero actions.
     *
     * @param idle animation used while standing still
     * @param run animation used while running
     * @param jump animation used while airborne
     * @param shoot animation used during the sword swing
     */
    public HeroSpriteSet(DirectionalAnimation idle,
                         DirectionalAnimation run,
                         DirectionalAnimation jump,
                         DirectionalAnimation shoot) {
        this.idle = idle;
        this.run = run;
        this.jump = jump;
        this.shoot = shoot;
    }

    /**
     * Picks the frame for the requested action and direction.
     *
     * @param action animation state
     * @param facingLeft true when the hero is facing left
     * @param stateTime elapsed time spent in the given action
     * @return texture region to draw or null when the animation is missing
     */
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
