package com.monzombie.game.world.geometry;

import com.badlogic.gdx.math.Rectangle;
import com.monzombie.game.util.Constants;

/**
 * Cette couche essaye de sauver le niveau si le tmx plante pour proteger le leaderboard.
 */
public class FallbackLayer implements GeometryLayer {

    /**
     * Ajoute un sol simple pour eviter les crash si le tmx manque.
     */
    @Override
    public void apply(GeometryContext ctx) {
        if (ctx.solids.size > 0) return;
        float w = Math.max(ctx.worldWidth, 800f);
        ctx.solids.add(new Rectangle(0f, 0f, w, Constants.GROUND_H));
        ctx.zones.add(new Rectangle(40f, Constants.GROUND_H, w - 80f, 200f));
    }
}
