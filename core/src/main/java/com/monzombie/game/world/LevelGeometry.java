package com.monzombie.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Sort;
import com.monzombie.game.util.Constants;
import java.util.Comparator;

/**
 * Builds collision, hazard and spawn zones by analyzing the level background image.
 */
public class LevelGeometry {

    private static final String MAP_PATH = "bunker.jpg";
    private static final float BOARD_SCAN_START = 0.45f;
    private static final float BOARD_SCAN_END   = 0.98f;
    private static final float BOARD_MERGE_GAP  = 18f;
    private static final float MIN_ZONE_WIDTH   = 140f;
    private static final float ZONE_PADDING     = 12f;
    private static final float BOARD_HEIGHT     = 150f;
    private static final float BOARD_SHRINK     = 12f;
    private static final float EXIT_CLEAR_W     = 200f;
    private static final float HOLE_SCAN_START  = 0.78f;
    private static final float HOLE_SCAN_END    = 0.97f;
    private static final float HOLE_THRESHOLD   = 53f;
    private static final int   MIN_HOLE_PIXELS  = 90;
    private static final float PLATFORM_HEIGHT  = 210f;
    private static final float PLATFORM_THICKNESS = 30f;
    private static final float PLATFORM_MIN_WIDTH = 60f;
    private static final int   MIN_CRATE_PIXELS = 280;

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

    // Bloc vert 1 (sol principal)
    private static final RectDef[] MANUAL_GROUNDS = new RectDef[]{
        new RectDef(0f, 0f, 730f, Constants.GROUND_H),             // Bloc sol 1
        new RectDef(870f, 0f, 550f, Constants.GROUND_H),           // Bloc sol 2
        new RectDef(1750f, 0f, 1400f, Constants.GROUND_H)            // Bloc sol 3
    };

    // Bloc vert plateformes
    private static final RectDef[] MANUAL_PLATFORMS = new RectDef[]{
        // Plateforme 1
//        new RectDef(640f, Constants.GROUND_H + 210f, 260f, 30f),   // Plateforme 2
//        new RectDef(1500f, Constants.GROUND_H + 120f, 220f, 30f)   // Plateforme 3
    };

    // Bloc rouge (trous / zones mortelles)
    private static final RectDef[] MANUAL_HAZARDS = new RectDef[]{
        new RectDef(730f, 890f, 150f, Constants.GROUND_H + 400f),    // Trou 1
        new RectDef(1400f, 0f, 400f, Constants.GROUND_H + 400f)      // Trou 2
    };

    // Bloc vert spawn zombies
    private static final RectDef[] MANUAL_ZONES = new RectDef[]{
        new RectDef(0f, Constants.GROUND_H, 730f, 220f),          // Spawn zone 1
        new RectDef(870f, Constants.GROUND_H, 550f, 220f),         // Spawn zone 2
        new RectDef(1750f, Constants.GROUND_H, 1400f, 220f)          // Spawn zone 3
    };

    private final Array<Rectangle> solids = new Array<>();
    private final Array<Rectangle> hazards = new Array<>();
    private final Array<Rectangle> zombieZones = new Array<>();
    private final float worldWidth;

    /**
     * Creates level geometry using the default bunker texture as the data source.
     *
     * @param worldWidth width of the playable world
     */
    public LevelGeometry(float worldWidth) {
        this(worldWidth, MAP_PATH);
    }

    /**
     * Creates level geometry from a custom image.
     *
     * @param worldWidth width of the playable world
     * @param mapPath asset path to the map texture
     */
    public LevelGeometry(float worldWidth, String mapPath) {
        this.worldWidth = worldWidth;
        buildFromMap(mapPath);
    }

    private void buildFromMap(String mapPath) {
        buildManualLayout();
    }

    private void buildManualLayout() {
        solids.clear();
        hazards.clear();
        zombieZones.clear();

        for (RectDef def : MANUAL_GROUNDS) addGround(def);
        for (RectDef def : MANUAL_PLATFORMS) addPlatform(def);
        for (RectDef def : MANUAL_HAZARDS) addHazard(def);
        for (RectDef def : MANUAL_ZONES) addZombieZone(def);
    }

    private void buildFromPixmap(Pixmap pixmap) {
        solids.clear();
        hazards.clear();
        zombieZones.clear();

        float scaleX = worldWidth / pixmap.getWidth();
        float scaleY = Constants.VH / (float) pixmap.getHeight();

        Array<Rectangle> holes = detectHoles(pixmap, scaleX);
        addHazardZones(holes);
        buildGroundSegments(holes);

        Array<Rectangle> boards = detectBoards(pixmap, scaleX);
        solids.addAll(boards);
        Array<Rectangle> crates = detectCratePlatforms(pixmap, scaleX, scaleY);
        solids.addAll(crates);
        addPlatformsOverHoles(holes);

        buildZones(boards, holes);
    }

    private Array<Rectangle> detectBoards(Pixmap pixmap, float scaleX) {
        Array<Rectangle> boards = new Array<>();
        int startY = (int)(pixmap.getHeight() * BOARD_SCAN_START);
        int endY   = (int)(pixmap.getHeight() * BOARD_SCAN_END);

        boolean inside = false;
        int startX = 0;

        for (int x = 0; x < pixmap.getWidth(); x++) {
            boolean columnHasBoard = false;
            for (int y = startY; y < endY; y++) {
                int pixel = pixmap.getPixel(x, y);
                int r = (pixel >>> 24) & 0xFF;
                int g = (pixel >>> 16) & 0xFF;
                int b = (pixel >>> 8) & 0xFF;
                if (isBoardColor(r, g, b)) {
                    columnHasBoard = true;
                    break;
                }
            }

            if (columnHasBoard) {
                if (!inside) {
                    inside = true;
                    startX = x;
                }
            } else if (inside) {
                boards.add(pixelRectToWorld(startX, x - 1, scaleX));
                inside = false;
            }
        }

        if (inside) {
            boards.add(pixelRectToWorld(startX, pixmap.getWidth() - 1, scaleX));
        }

        mergeAdjacentBoards(boards);
        trimExitArea(boards);
        Sort.instance().sort(boards, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return Float.compare(o1.x, o2.x);
            }
        });
        return boards;
    }

    private Rectangle pixelRectToWorld(int startX, int endX, float scaleX) {
        float x = startX * scaleX;
        float width = (endX - startX + 1) * scaleX;
        x += BOARD_SHRINK;
        width = Math.max(20f, width - BOARD_SHRINK * 2f);
        Rectangle rect = new Rectangle(x, Constants.GROUND_H, width, BOARD_HEIGHT);
        logRectY("Plateforme détectée", rect);
        return rect;
    }

    private boolean isBoardColor(int r, int g, int b) {
        return r > g + 25 && r > b + 25 && g > 60;
    }

    private void mergeAdjacentBoards(Array<Rectangle> boards) {
        Sort.instance().sort(boards, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return Float.compare(o1.x, o2.x);
            }
        });
        for (int i = boards.size - 2; i >= 0; i--) {
            Rectangle current = boards.get(i);
            Rectangle next = boards.get(i + 1);
            float gap = next.x - (current.x + current.width);
            if (gap <= BOARD_MERGE_GAP) {
                float right = Math.max(current.x + current.width, next.x + next.width);
                current.width = right - current.x;
                boards.removeIndex(i + 1);
            }
        }
    }

    private Array<Rectangle> detectHoles(Pixmap pixmap, float scaleX) {
        Array<Rectangle> holes = new Array<>();
        int height = pixmap.getHeight();
        int startY = Math.min(height - 2, Math.max(0, (int)(height * HOLE_SCAN_START)));
        int endY = Math.min(height - 1, Math.max(startY + 1, (int)(height * HOLE_SCAN_END)));

        boolean inside = false;
        int startX = 0;

        for (int x = 0; x < pixmap.getWidth(); x++) {
            float lum = columnAverageLuminance(pixmap, x, startY, endY);
            boolean isHole = lum < HOLE_THRESHOLD;
            if (isHole && !inside) {
                inside = true;
                startX = x;
            } else if (!isHole && inside) {
                appendHole(holes, startX, x - 1, scaleX);
                inside = false;
            }
        }

        if (inside) {
            appendHole(holes, startX, pixmap.getWidth() - 1, scaleX);
        }
        return holes;
    }

    private float columnAverageLuminance(Pixmap pixmap, int x, int startY, int endY) {
        float sum = 0f;
        int count = 0;
        for (int y = startY; y <= endY; y++) {
            int pixel = pixmap.getPixel(x, y);
            int r = (pixel >>> 24) & 0xFF;
            int g = (pixel >>> 16) & 0xFF;
            int b = (pixel >>> 8) & 0xFF;
            sum += (r + g + b) / 3f;
            count++;
        }
        return count == 0 ? 0f : sum / count;
    }

    private void appendHole(Array<Rectangle> holes, int startPx, int endPx, float scaleX) {
        int widthPx = endPx - startPx + 1;
        if (widthPx < MIN_HOLE_PIXELS) return;
        float hx = startPx * scaleX;
        float hw = widthPx * scaleX;
        Rectangle rect = new Rectangle(hx, -400f, hw, Constants.GROUND_H + 520f);
        holes.add(rect);
        logRectY("Trou détecté", rect);
    }

    private void addHazardZones(Array<Rectangle> holes) {
        if (holes == null) return;
        for (Rectangle hole : holes) {
            Rectangle hazard = new Rectangle(hole.x, 0f, hole.width, Constants.GROUND_H + 260f);
            hazards.add(hazard);
            logRectY("Zone mortelle", hazard);
        }
    }

    private void buildGroundSegments(Array<Rectangle> holes) {
        if (holes == null || holes.size == 0) {
            solids.add(new Rectangle(0f, 0f, worldWidth, Constants.GROUND_H));
            return;
        }

        Array<Rectangle> sorted = new Array<>(holes);
        Sort.instance().sort(sorted, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return Float.compare(o1.x, o2.x);
            }
        });

        float cursor = 0f;
        for (Rectangle hole : sorted) {
            float width = hole.x - cursor;
            if (width > 1f) {
                solids.add(new Rectangle(cursor, 0f, width, Constants.GROUND_H));
            }
            cursor = Math.max(cursor, hole.x + hole.width);
        }

        if (worldWidth - cursor > 1f) {
            solids.add(new Rectangle(cursor, 0f, worldWidth - cursor, Constants.GROUND_H));
        }
    }

    private Array<Rectangle> detectCratePlatforms(Pixmap pixmap, float scaleX, float scaleY) {
        Array<Rectangle> crates = new Array<>();
        int width = pixmap.getWidth();
        int height = pixmap.getHeight();
        int scanStartY = (int)(height * 0.3f);
        scanStartY = Math.max(0, Math.min(height - 1, scanStartY));

        boolean[] visited = new boolean[width * height];
        IntArray stack = new IntArray();

        for (int y = scanStartY; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = y * width + x;
                if (visited[idx]) continue;

                int pixel = pixmap.getPixel(x, y);
                int r = (pixel >>> 24) & 0xFF;
                int g = (pixel >>> 16) & 0xFF;
                int b = (pixel >>> 8) & 0xFF;
                if (!isCrateColor(r, g, b)) continue;

                visited[idx] = true;
                stack.add(idx);

                int minX = x;
                int maxX = x;
                int minY = y;
                int maxY = y;
                int count = 0;

                while (stack.size > 0) {
                    int current = stack.removeIndex(stack.size - 1);
                    int cx = current % width;
                    int cy = current / width;
                    count++;
                    if (cx < minX) minX = cx;
                    if (cx > maxX) maxX = cx;
                    if (cy < minY) minY = cy;
                    if (cy > maxY) maxY = cy;

                    for (int dir = 0; dir < 4; dir++) {
                        int nx = cx + (dir == 0 ? 1 : dir == 1 ? -1 : 0);
                        int ny = cy + (dir == 2 ? 1 : dir == 3 ? -1 : 0);
                        if (nx < 0 || nx >= width || ny < scanStartY || ny >= height) continue;
                        int nIdx = ny * width + nx;
                        if (visited[nIdx]) continue;
                        int np = pixmap.getPixel(nx, ny);
                        int nr = (np >>> 24) & 0xFF;
                        int ng = (np >>> 16) & 0xFF;
                        int nb = (np >>> 8) & 0xFF;
                        if (!isCrateColor(nr, ng, nb)) continue;
                        visited[nIdx] = true;
                        stack.add(nIdx);
                    }
                }

                if (count < MIN_CRATE_PIXELS) continue;
                int pixelWidth = maxX - minX + 1;
                int pixelHeight = maxY - minY + 1;
                if (pixelWidth < 20 || pixelHeight < 15) continue;

                float worldX = minX * scaleX;
                float worldWidth = pixelWidth * scaleX;
                float worldHeight = pixelHeight * scaleY;
                float worldY = (height - (maxY + 1)) * scaleY;
                crates.add(new Rectangle(worldX, worldY, worldWidth, worldHeight));
            }
        }

        return crates;
    }

    private boolean isCrateColor(int r, int g, int b) {
        if (r < 110 || g < 55) return false;
        if (b > 150) return false;
        if (r - g < 20) return false;
        if (g - b < 10) return false;
        return true;
    }

    private void addPlatformsOverHoles(Array<Rectangle> holes) {
        if (holes == null) return;
        for (Rectangle hole : holes) {
            float pad = Math.min(50f, hole.width * 0.2f);
            float width = Math.max(PLATFORM_MIN_WIDTH, hole.width - pad * 2f);
            if (width <= 0f) continue;
            float x = hole.x + pad;
            float y = Constants.GROUND_H + PLATFORM_HEIGHT;
            solids.add(new Rectangle(x, y, width, PLATFORM_THICKNESS));
        }
    }

    private void buildZones(Array<Rectangle> boards, Array<Rectangle> holes) {
        zombieZones.clear();
        Array<Rectangle> blockers = new Array<>();
        if (holes != null) blockers.addAll(holes);
        if (boards != null) blockers.addAll(boards);

        if (blockers.size == 0) {
            float zoneStart = ZONE_PADDING;
            float zoneEnd = worldWidth - ZONE_PADDING;
            if (zoneEnd - zoneStart >= MIN_ZONE_WIDTH) {
                zombieZones.add(new Rectangle(zoneStart, Constants.GROUND_H, zoneEnd - zoneStart, 220f));
            }
            return;
        }

        Sort.instance().sort(blockers, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return Float.compare(o1.x, o2.x);
            }
        });

        float cursor = 0f;
        for (Rectangle blocker : blockers) {
            float zoneStart = cursor + ZONE_PADDING;
            float zoneEnd = blocker.x - ZONE_PADDING;
            if (zoneEnd - zoneStart >= MIN_ZONE_WIDTH) {
                zombieZones.add(new Rectangle(zoneStart, Constants.GROUND_H, zoneEnd - zoneStart, 220f));
            }
            float newCursor = blocker.x + blocker.width;
            if (newCursor > cursor) cursor = newCursor;
        }

        float zoneStart = cursor + ZONE_PADDING;
        float zoneEnd = worldWidth - ZONE_PADDING;
        if (zoneEnd - zoneStart >= MIN_ZONE_WIDTH) {
            zombieZones.add(new Rectangle(zoneStart, Constants.GROUND_H, zoneEnd - zoneStart, 220f));
        }
    }

    private void trimExitArea(Array<Rectangle> boards) {
        float limit = worldWidth - EXIT_CLEAR_W;
        for (int i = boards.size - 1; i >= 0; i--) {
            Rectangle board = boards.get(i);
            if (board.x >= limit) {
                boards.removeIndex(i);
                continue;
            }
            float right = board.x + board.width;
            if (right > limit) {
                board.width = limit - board.x;
                if (board.width < 20f) boards.removeIndex(i);
            }
        }
    }

    private void buildFallback() {
        solids.clear();
        hazards.clear();
        zombieZones.clear();

        solids.add(new Rectangle(0f, 0f, worldWidth, Constants.GROUND_H));

        float section = worldWidth / 4f;
        for (int i = 1; i < 4; i++) {
            float x = i * section - 30f;
            solids.add(new Rectangle(x, Constants.GROUND_H, 60f, Constants.VH - Constants.GROUND_H));
        }

        float last = 0f;
        for (Rectangle solid : solids) {
            if (solid.width >= worldWidth - 1f) continue;
            float zoneStart = last + ZONE_PADDING;
            float zoneEnd = solid.x - ZONE_PADDING;
            if (zoneEnd - zoneStart >= MIN_ZONE_WIDTH) {
                zombieZones.add(new Rectangle(zoneStart, Constants.GROUND_H, zoneEnd - zoneStart, 220f));
            }
            last = solid.x + solid.width;
        }
    }

    private void logRectY(String label, Rectangle rectangle) {
        System.out.println(label + " | x=" + rectangle.x + " y=" + rectangle.y + " largeur=" + rectangle.width + " hauteur=" + rectangle.height);
    }

    private void addGround(RectDef def) {
        Rectangle rect = new Rectangle(def.x, def.y, def.width, def.height);
        solids.add(rect);
        logRectY("Sol manuel", rect);
    }

    private void addPlatform(RectDef def) {
        Rectangle rect = new Rectangle(def.x, def.y, def.width, def.height);
        solids.add(rect);
        logRectY("Plateforme manuelle", rect);
    }

    private void addHazard(RectDef def) {
        float hazardY = def.y;
        if (hazardY >= Constants.VH || hazardY <= -Constants.VH) {
            // Align with the ground when the manual value sits outside the visible map.
            hazardY = 0f;
        }
        float hazardHeight = Math.max(def.height, Constants.GROUND_H + 10f);
        Rectangle rect = new Rectangle(def.x, hazardY, def.width, hazardHeight);
        hazards.add(rect);
        logRectY("Zone mortelle manuelle", rect);
    }

    private void addZombieZone(RectDef def) {
        Rectangle rect = new Rectangle(def.x, def.y, def.width, def.height);
        zombieZones.add(rect);
        logRectY("Zone zombies manuelle", rect);
    }

    /**
     * @return rectangles that should be treated as solid ground or walls.
     */
    public Array<Rectangle> getSolids() {
        return solids;
    }

    /**
     * @return rectangles that kill the player upon collision.
     */
    public Array<Rectangle> getHazards() {
        return hazards;
    }

    /**
     * @return patrol zones that also serve as spawn areas for zombies.
     */
    public Array<Rectangle> getZombieZones() {
        return zombieZones;
    }
}
