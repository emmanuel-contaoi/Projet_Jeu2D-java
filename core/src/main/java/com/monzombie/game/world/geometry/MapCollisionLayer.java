package com.monzombie.game.world.geometry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.monzombie.game.util.Constants;
import java.util.Locale;

/**
 * Charge les collisions depuis le fichier tmx indique.
 */
public class MapCollisionLayer implements GeometryLayer {
    private static final String[] SUPPORTED_COLLISION_NAMES = {
        "colision",
        "collision",
        "collisions",
        "obstacle",
        "obstacles"
    };
    private final String mapPath;

    /**
     * Retient le chemin pour reparser la map si besoin.
     */
    public MapCollisionLayer(String mapPath) {
        this.mapPath = mapPath;
    }

    /**
     * Parse le fichier xml et met les rectangles en monde reel.
     */
    @Override
    public void apply(GeometryContext ctx) {
        FileHandle file = Gdx.files.internal(mapPath);
        if (!file.exists()) return;
        XmlReader.Element root;
        try {
            root = new XmlReader().parse(file);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lecture map " + mapPath, e);
        }
        int width = root.getIntAttribute("width", 0);
        int height = root.getIntAttribute("height", 0);
        int tileWidth = root.getIntAttribute("tilewidth", 32);
        int tileHeight = root.getIntAttribute("tileheight", 32);
        ctx.mapWidth = width * tileWidth;
        ctx.mapHeight = height * tileHeight;
        if (ctx.mapWidth <= 0f || ctx.mapHeight <= 0f) return;
        float scaleX = ctx.worldWidth / ctx.mapWidth;
        float scaleY = Constants.VH / ctx.mapHeight;
        Array<XmlReader.Element> layers = root.getChildrenByName("objectgroup");
        for (int i = 0; i < layers.size; i++) {
            XmlReader.Element group = layers.get(i);
            String groupName = group.getAttribute("name", "");
            if (!isCollisionGroup(groupName)) continue;
            appendObjects(group, ctx, scaleX, scaleY);
        }
    }

    /**
     * Verifie si le groupe correspond a une couche de collision connue.
     */
    private boolean isCollisionGroup(String name) {
        if (name == null) return false;
        String normalized = name.toLowerCase(Locale.ROOT);
        for (String candidate : SUPPORTED_COLLISION_NAMES) {
            if (candidate.equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converti chaque objet xml en rectangle monde.
     */
    private void appendObjects(XmlReader.Element group, GeometryContext ctx, float scaleX, float scaleY) {
        Array<XmlReader.Element> objects = group.getChildrenByName("object");
        for (int j = 0; j < objects.size; j++) {
            XmlReader.Element obj = objects.get(j);
            Rectangle rect = rectangleFromObject(obj, ctx, scaleX, scaleY);
            if (rect != null) {
                ctx.solids.add(rect);
            }
        }
    }

    private Rectangle rectangleFromObject(XmlReader.Element obj, GeometryContext ctx, float scaleX, float scaleY) {
        float w = obj.getFloatAttribute("width", 0f);
        float h = obj.getFloatAttribute("height", 0f);
        if (w > 0f && h > 0f) {
            float x = obj.getFloatAttribute("x", 0f);
            float yTop = obj.getFloatAttribute("y", 0f);
            float worldX = x * scaleX;
            float worldW = w * scaleX;
            float bottom = ctx.mapHeight - (yTop + h);
            float worldY = bottom * scaleY;
            float worldH = h * scaleY;
            return new Rectangle(worldX, worldY, worldW, worldH);
        }
        Array<XmlReader.Element> polygons = obj.getChildrenByName("polygon");
        if (polygons != null && polygons.size > 0) {
            return rectangleFromPolygon(obj, polygons.get(0), ctx, scaleX, scaleY);
        }
        return null;
    }

    private Rectangle rectangleFromPolygon(XmlReader.Element obj,
                                           XmlReader.Element polygon,
                                           GeometryContext ctx,
                                           float scaleX,
                                           float scaleY) {
        if (polygon == null) return null;
        String pointsStr = polygon.getAttribute("points", "").trim();
        if (pointsStr.isEmpty()) return null;
        String[] points = pointsStr.split(" ");
        if (points.length == 0) return null;
        float baseX = obj.getFloatAttribute("x", 0f);
        float baseY = obj.getFloatAttribute("y", 0f);
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        for (String point : points) {
            if (point == null || point.isEmpty()) continue;
            String[] coords = point.split(",");
            if (coords.length != 2) continue;
            float dx = parseFloat(coords[0]);
            float dy = parseFloat(coords[1]);
            float px = baseX + dx;
            float py = baseY + dy;
            if (px < minX) minX = px;
            if (px > maxX) maxX = px;
            if (py < minY) minY = py;
            if (py > maxY) maxY = py;
        }
        if (!(minX < maxX && minY < maxY)) return null;
        float worldX = minX * scaleX;
        float worldW = (maxX - minX) * scaleX;
        float worldY = (ctx.mapHeight - maxY) * scaleY;
        float worldH = (maxY - minY) * scaleY;
        return new Rectangle(worldX, worldY, worldW, worldH);
    }

    private float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0f;
        }
    }
}
