package com.monzombie.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.monzombie.game.util.Constants;
import java.util.Comparator;

public class LevelGeometry {

    private static final String MAP_PATH = "map niv1 bunker..tmx";

    private static final class RectDef {
        final float x;
        final float y;
        final float width;
        final float height;

        RectDef(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private static final RectDef[] ZOMBIE_ZONES = new RectDef[]{
        new RectDef(0f, Constants.GROUND_H, 730f, 220f),
        new RectDef(870f, Constants.GROUND_H, 550f, 220f),
        new RectDef(1750f, Constants.GROUND_H, 1400f, 220f)
    };

    private final Array<Rectangle> solids = new Array<>();
    private final Array<Rectangle> hazards = new Array<>();
    private final Array<Rectangle> zones = new Array<>();
    private final float worldWidth;

    public LevelGeometry(float worldWidth) {
        this(worldWidth, MAP_PATH);
    }

    public LevelGeometry(float worldWidth, String mapPath) {
        this.worldWidth = worldWidth;
        load(mapPath);
    }

    private void load(String mapPath) {
        solids.clear();
        hazards.clear();
        zones.clear();
        readMap(mapPath);
        for (RectDef def : ZOMBIE_ZONES) {
            zones.add(new Rectangle(def.x, def.y, def.width, def.height));
        }
        buildHolesFromGround();
    }

    private void readMap(String mapPath) {
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
        float mapW = width * tileWidth;
        float mapH = height * tileHeight;

        if (mapW <= 0f || mapH <= 0f) return;

        float scaleX = worldWidth / mapW;
        float scaleY = Constants.VH / mapH;

        Array<XmlReader.Element> groups = root.getChildrenByName("objectgroup");
        for (int i = 0; i < groups.size; i++) {
            XmlReader.Element group = groups.get(i);
            String name = group.getAttribute("name", "");
            if (!"colision".equalsIgnoreCase(name)) continue;
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
                float bottom = mapH - (yTop + h);
                float worldY = bottom * scaleY;
                float worldH = h * scaleY;
                solids.add(new Rectangle(worldX, worldY, worldW, worldH));
            }
        }
    }

    private void buildHolesFromGround() {
        Array<Rectangle> ground = new Array<>();
        float tolerance = 6f;
        for (Rectangle rect : solids) {
            boolean touchesGround = rect.y <= Constants.GROUND_H + tolerance
                && rect.y + rect.height >= Constants.GROUND_H - tolerance;
            if (touchesGround) ground.add(rect);
        }
        if (ground.isEmpty()) return;
        ground.sort(new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return Float.compare(o1.x, o2.x);
            }
        });
        float cursor = 0f;
        for (Rectangle rect : ground) {
            float left = rect.x;
            float right = rect.x + rect.width;
            if (right <= cursor) continue;
            if (left > cursor + 1f) {
                addHole(cursor, left - cursor);
            }
            if (right > cursor) cursor = right;
        }
        if (cursor < worldWidth) {
            addHole(cursor, worldWidth - cursor);
        }
    }

    private void addHole(float x, float width) {
        if (width < 25f) return;
        float hazardHeight = Constants.GROUND_H + 400f;
        hazards.add(new Rectangle(x, 0f, width, hazardHeight));
    }

    public Array<Rectangle> getSolids() {
        return solids;
    }

    public Array<Rectangle> getHazards() {
        return hazards;
    }

    public Array<Rectangle> getZombieZones() {
        return zones;
    }
}
