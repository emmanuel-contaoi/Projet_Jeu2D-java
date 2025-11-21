package com.monzombie.game.world.geometry;

/**
 * Petit container pour definir les rectangle figes.
 */
public class RectDef {
    public final float x;
    public final float y;
    public final float width;
    public final float height;

    /**
     * Cree un rectangle tout simple.
     */
    public RectDef(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
