package com.monzombie.game.world.geometry;

import com.badlogic.gdx.math.Rectangle;

/**
 * Ajoute les zones manuelles pour garder les spawn stables.
 */
public class ManualZoneLayer implements GeometryLayer {
    private final RectDef[] defs;

    /**
     * Recoit la liste de zones prepare a l avance.
     */
    public ManualZoneLayer(RectDef[] defs) {
        this.defs = defs;
    }

    /**
     * Injecte directement les zones de spawn fixe.
     */
    @Override
    public void apply(GeometryContext ctx) {
        if (defs == null) return;
        for (RectDef def : defs) {
            ctx.zones.add(new Rectangle(def.x, def.y, def.width, def.height));
        }
    }
}
