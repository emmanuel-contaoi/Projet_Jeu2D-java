package com.monzombie.game.world.geometry;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Contient les listes partagees pour construire la map.
 */
public class GeometryContext {
    public final float worldWidth;
    public final Array<Rectangle> solids = new Array<>();
    public final Array<Rectangle> hazards = new Array<>();
    public final Array<Rectangle> zones = new Array<>();
    public float mapWidth;
    public float mapHeight;

    /**
     * Cree un contexte simple avec la largeur monde.
     */
    public GeometryContext(float worldWidth) {
        this.worldWidth = worldWidth;
    }

    /**
     * Vide les listes pour regenerer la map sans planter le score.
     */
    public void reset() {
        solids.clear();
        hazards.clear();
        zones.clear();
        mapWidth = 0f;
        mapHeight = 0f;
    }
}
