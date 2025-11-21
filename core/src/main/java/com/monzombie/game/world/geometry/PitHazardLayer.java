package com.monzombie.game.world.geometry;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.monzombie.game.util.Constants;
import java.util.Comparator;

/**
 * Cree les trou mortels pour eviter que les joueurs profitent du leaderboard.
 */
public class PitHazardLayer implements GeometryLayer {
    private static final float TOLERANCE = 6f;
    private static final float INSET = 18f;
    private static final float DEPTH = Constants.GROUND_H * 2.3f;
    private static final float HAZARD_HEIGHT = Constants.GROUND_H;

    /**
     * Observe les sols pour construire les trous.
     */
    @Override
    public void apply(GeometryContext ctx) {
        Array<Rectangle> ground = filterGround(ctx.solids);
        if (ground.isEmpty()) return;
        ground.sort(new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle a, Rectangle b) {
                return Float.compare(a.x, b.x);
            }
        });
        float cursor = 0f;
        for (Rectangle rect : ground) {
            float left = rect.x;
            float right = rect.x + rect.width;
            if (right <= cursor) continue;
            if (left > cursor + 1f) {
                addHole(ctx, cursor, left - cursor);
            }
            if (right > cursor) cursor = right;
        }
        if (cursor < ctx.worldWidth) {
            addHole(ctx, cursor, ctx.worldWidth - cursor);
        }
    }

    /**
     * Filtre juste les plateformes collee au sol.
     */
    private Array<Rectangle> filterGround(Array<Rectangle> solids) {
        Array<Rectangle> ground = new Array<>();
        for (Rectangle rect : solids) {
            boolean touches = rect.y <= Constants.GROUND_H + TOLERANCE && rect.y + rect.height >= Constants.GROUND_H - TOLERANCE;
            if (touches) ground.add(rect);
        }
        return ground;
    }

    /**
     * Ajoute une zone mortel en evitant les bords.
     */
    private void addHole(GeometryContext ctx, float x, float width) {
        if (width < 25f) return;
        float innerWidth = width - INSET * 2f;
        if (innerWidth <= 8f) return;
        float y = -DEPTH;
        Rectangle rect = new Rectangle(x + INSET, y, innerWidth, HAZARD_HEIGHT);
        ctx.hazards.add(rect);
        System.out.println("Zone mortelle creee x=" + rect.x + " w=" + rect.width + " h=" + rect.height);
    }
}
