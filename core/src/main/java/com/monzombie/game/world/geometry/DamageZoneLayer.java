package com.monzombie.game.world.geometry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.monzombie.game.util.Constants;
import java.util.Locale;

/**
 * Parse les zones de degats/death depuis les layer TMX pour creer des hazards.
 */
public class DamageZoneLayer implements GeometryLayer {

    private static final String[] SUPPORTED_GROUPS = {
        "degats",
        "degat",
        "damage",
        "hazard",
        "hazards"
    };

    private final String mapPath;

    public DamageZoneLayer(String mapPath) {
        this.mapPath = mapPath;
    }

    @Override
    public void apply(GeometryContext ctx) {
        FileHandle file = Gdx.files.internal(mapPath);
        if (!file.exists()) return;
        XmlReader.Element root;
        try {
            root = new XmlReader().parse(file);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lecture zones degats " + mapPath, e);
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
            if (!matches(groupName)) continue;
            appendZones(group, ctx, scaleX, scaleY);
        }
    }

    private void appendZones(XmlReader.Element group, GeometryContext ctx, float scaleX, float scaleY) {
        Array<XmlReader.Element> objects = group.getChildrenByName("object");
        for (int j = 0; j < objects.size; j++) {
            XmlReader.Element obj = objects.get(j);
            float w = obj.getFloatAttribute("width", 0f);
            float h = obj.getFloatAttribute("height", 0f);
            if (w <= 0f || h <= 0f) continue;
            float x = obj.getFloatAttribute("x", 0f);
            float yTop = obj.getFloatAttribute("y", 0f);
            float worldX = x * scaleX;
            float worldW = w * scaleX;
            float bottom = ctx.mapHeight - (yTop + h);
            float worldY = bottom * scaleY;
            float worldH = h * scaleY;
            ctx.hazards.add(new Rectangle(worldX, worldY, worldW, worldH));
        }
    }

    private boolean matches(String name) {
        if (name == null) return false;
        String normalized = name.toLowerCase(Locale.ROOT);
        for (String n : SUPPORTED_GROUPS) {
            if (n.equals(normalized)) return true;
        }
        return false;
    }
}
