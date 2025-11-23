package com.monzombie.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.monzombie.game.util.Constants;
import com.monzombie.game.world.geometry.DamageZoneLayer;
import com.monzombie.game.world.geometry.FallbackLayer;
import com.monzombie.game.world.geometry.GeometryContext;
import com.monzombie.game.world.geometry.GeometryLayer;
import com.monzombie.game.world.geometry.ManualZoneLayer;
import com.monzombie.game.world.geometry.MapCollisionLayer;
import com.monzombie.game.world.geometry.PitHazardLayer;
import com.monzombie.game.world.geometry.RectDef;

/**
 * Gere la geometrie du niveau pour eviter que les scores bug quand la map change.
 */
public class LevelGeometry {
    private static final String MAP_PATH = "map niv1 bunker..tmx";
    private static final String FOREST_MAP_PATH = "foret.tmx";

    private static final RectDef[] ZOMBIE_ZONES = new RectDef[]{
        new RectDef(0f, Constants.GROUND_H, 730f, 220f),
        new RectDef(870f, Constants.GROUND_H, 550f, 220f),
        new RectDef(1750f, Constants.GROUND_H, 1400f, 220f)
    };

    private final GeometryContext ctx;
    private final Array<GeometryLayer> layers = new Array<>();

    /**
     * Construit la geometrie par defaut du bunker.
     */
    public LevelGeometry(float worldWidth) {
        this(worldWidth, MAP_PATH);
    }

    /**
     * Construit la geometrie depuis un chemin personnalise tmx.
     */
    public LevelGeometry(float worldWidth, String mapPath) {
        ctx = new GeometryContext(worldWidth);
        layers.add(new MapCollisionLayer(mapPath));
        layers.add(new DamageZoneLayer(mapPath));
        layers.add(new FallbackLayer());
        layers.add(new ManualZoneLayer(ZOMBIE_ZONES));
        layers.add(new PitHazardLayer());
        rebuild();
    }

    /**
     * Selectionne la map appropriee suivant le numero de niveau.
     */
    public static LevelGeometry forLevel(float worldWidth, int levelNumber) {
        String mapPath = MAP_PATH;
        if (levelNumber == 2) {
            mapPath = FOREST_MAP_PATH;
        }
        return new LevelGeometry(worldWidth, mapPath);
    }

    /**
     * Recharge toutes les couches pour garder la partie clean.
     */
    private void rebuild() {
        ctx.reset();
        for (int i = 0; i < layers.size; i++) {
            layers.get(i).apply(ctx);
        }
    }

    /**
     * Renvoi les collider solides pour la physique.
     */
    public Array<Rectangle> getSolids() {
        return ctx.solids;
    }

    /**
     * Renvoi les zones mortel generees depuis les trou.
     */
    public Array<Rectangle> getHazards() {
        return ctx.hazards;
    }

    /**
     * Renvoi les zones de spawn pour les zombies.
     */
    public Array<Rectangle> getZombieZones() {
        return ctx.zones;
    }
}
