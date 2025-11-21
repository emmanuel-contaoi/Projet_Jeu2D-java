package com.monzombie.game.entity;

import com.badlogic.gdx.math.Rectangle;

/**
 * Classe de base toute simple pour mutualiser la position et les hitbox.
 */
public abstract class AbstractEntity {
    public float x;
    public float y;
    public float w;
    public float h;
    protected final Rectangle bounds = new Rectangle();

    /**
     * Prepare une entite avec une largeur et hauteur fixe.
     */
    protected AbstractEntity(float width, float height) {
        this.w = width;
        this.h = height;
    }

    /**
     * Retourne un rectangle par defaut couvrant tout le sprite.
     */
    public Rectangle getBounds() {
        bounds.set(x, y, w, h);
        return bounds;
    }
}
