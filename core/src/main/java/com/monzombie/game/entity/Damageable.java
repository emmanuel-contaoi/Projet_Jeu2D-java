package com.monzombie.game.entity;

/**
 * Interface pour toutes les entites qui peuvent prendre des degats.
 */
public interface Damageable {
    /**
     * Inflige une quantite de degat positive.
     */
    void subirDegats(int quantite);

    /**
     * Indique si l entite est consideree comme morte.
     */
    boolean estMort();
}
