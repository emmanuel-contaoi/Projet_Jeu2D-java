package com.monzombie.game.world.geometry;

/**
 * Interface toute bete pour appliquer des couches sur la map.
 */
public interface GeometryLayer {
    /**
     * Applique la transformation sur le contexte courant.
     */
    void apply(GeometryContext ctx);
}
