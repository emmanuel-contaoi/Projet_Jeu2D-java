package com.monzombie.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import java.util.Locale;

/**
 * Loads and owns every texture and animation used across the game.
 */
public class Assets {

    
    public Texture bg;                
    public Texture groundTile;        
    public Texture soldierSheet;      
    public Texture zombieSheet;       
    public Texture hearts;            
    public Texture hugoSwordFL;
    public Texture hugoSwordFR;
    public Texture hugoJumpFL;
    public Texture hugoJumpFR;
    public Texture hugoMitrailletteFL;
    public Texture hugoMitrailletteFR;
    public Texture hugoPistoletFL;
    public Texture hugoPistoletFR;
    public Texture alexisSwordFL;
    public Texture alexisSwordFR;
    public Texture alexisJumpFL;
    public Texture alexisJumpFR;
    public Texture alexisMitrailletteFL;
    public Texture alexisMitrailletteFR;
    public Texture alexisPistoletFL;
    public Texture alexisPistoletFR;
    public Texture hugoJumpMinigun;

    public HeroSpriteSet hugoSprites;
    public HeroSpriteSet hugoGunSprites;
    public HeroSpriteSet alexisSprites;

    
    public Animation<TextureRegion> animWalk, animRun, animShot;
    public TextureRegion frameIdle;

    
    public Animation<TextureRegion> zombieWalk;

    
    public TextureRegion heartFull, heartEmpty;

    /**
     * Loads textures, builds hero animations and slices UI sprites.
     */
    public void load() {
        bg = loadTexture("bunker.jpg");
        soldierSheet = loadTexture("sprite sheet 8-bit 2.png", "hero.png");
        zombieSheet  = loadTexture("zombie_sheet.png");
        hearts       = loadTexture("hearts.png");
        hugoSwordFL  = loadTextureOrNull("HugoSwordFL.png", "Sprite /player/hugo/hugomoveattackFL.png");
        hugoSwordFR  = loadTextureOrNull("HugoSwordFR.png");
        hugoJumpFL   = loadTextureOrNull("HugoJumpingFL.png", "Sprite /player/hugo/hugojumpattacksword.png");
        hugoJumpFR   = loadTextureOrNull("HugoJumpingFR.png", "Sprite /player/hugo/hugojumpattacksword.png");
        hugoMitrailletteFL = loadTextureOrNull("HugoMitrailletteFL.png", "Sprite /player/hugo/hugomoveattackgatling.png");
        hugoMitrailletteFR = loadTextureOrNull("HugoMitrailletteFR.png", "Sprite /player/hugo/hugomoveattackminigun.png");
        hugoPistoletFL = loadTextureOrNull("HugoPistoletFL.png", "Sprite /player/hugo/hugojumpattacksword.png");
        hugoPistoletFR = loadTextureOrNull("HugoPistoletFR.png");
        hugoJumpMinigun = loadTextureOrNull("Sprite /player/hugo/hugojumpminigun.png");
        alexisSwordFL = loadTextureOrNull("AlexisSwordFL.png", "Sprite /player/alexis/alexiswalkwithwoodsword.png");
        alexisSwordFR = loadTextureOrNull("AlexisSwordFR.png");
        alexisJumpFL  = loadTextureOrNull("AlexisJumpingFL.png", "Sprite /player/alexis/alexisjumpminigun.png");
        alexisJumpFR  = loadTextureOrNull("AlexisJumpingFR.png");
        alexisMitrailletteFL = loadTextureOrNull("AlexisMitrailletteFL.png", "Sprite /player/alexis/alexiswalkminigun.png");
        alexisMitrailletteFR = loadTextureOrNull("AlexisMitrailletteFR.png", "alexisGunwalk.png");
        alexisPistoletFL = loadTextureOrNull("AlexisPistoletFL.png", "Sprite /player/alexis/alexisjumpgatling.png");
        alexisPistoletFR = loadTextureOrNull("AlexisPistoletFR.png", "alexisGunwalk.png");

        
        groundTile = makeGroundTile(64, 64);
        groundTile.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        buildPlayerAnims();
        buildZombieAnim();
        sliceHearts();

        buildHeroSpriteSets();
    }

    private Texture loadTexture(String... names) {
        Texture texture = loadTextureOrNull(names);
        if (texture == null) {
            throw new RuntimeException("Asset introuvable: " + joinNames(names) + " (place-le dans core/assets/)");
        }
        return texture;
    }

    private Texture loadTextureOrNull(String... names) {
        if (names == null) return null;
        for (String name : names) {
            if (name == null || name.isEmpty()) continue;
            FileHandle fh = Gdx.files.internal(name);
            if (!fh.exists()) continue;
            Texture texture = new Texture(fh);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            return texture;
        }
        return null;
    }

    private String joinNames(String... names) {
        if (names == null || names.length == 0) return "inconnu";
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            if (name == null || name.isEmpty()) continue;
            if (sb.length() > 0) sb.append(" ou ");
            sb.append(name);
        }
        return sb.length() == 0 ? "inconnu" : sb.toString();
    }

    private void buildPlayerAnims() {
        final int COLS = 4, ROWS = 6;
        int fw = soldierSheet.getWidth()/COLS, fh = soldierSheet.getHeight()/ROWS;

        animWalk = makeAnimRow(soldierSheet, ROWS, COLS, 0, 0, 2, 10f, Animation.PlayMode.LOOP);
        animRun  = makeAnimRow(soldierSheet, ROWS, COLS, 2, 0, 2, 12f, Animation.PlayMode.LOOP);
        animShot = makeAnimRow(soldierSheet, ROWS, COLS, 0, 3, 3, 18f, Animation.PlayMode.NORMAL);

        frameIdle = new TextureRegion(soldierSheet, 1*fw, 0*fh, fw, fh);
    }

    private void buildZombieAnim() {
        final int COLS = 4, ROWS = 2;
        Array<TextureRegion> list = new Array<>();
        for (TextureRegion tr : sliceRow(zombieSheet, COLS, ROWS, 0, 0, 3)) list.add(tr);
        for (TextureRegion tr : sliceRow(zombieSheet, COLS, ROWS, 1, 0, 3)) list.add(tr);
        zombieWalk = new Animation<>(1f/8f, list, Animation.PlayMode.LOOP);
    }

    private void sliceHearts() {
        
        int COLS=5, ROWS=3;
        int hw = hearts.getWidth()/COLS;
        int hh = hearts.getHeight()/ROWS;
        heartFull  = new TextureRegion(hearts, 0*hw, 0*hh, hw, hh); 
        heartEmpty = new TextureRegion(hearts, 2*hw, 1*hh, hw, hh); 
    }

    private Animation<TextureRegion> makeAnimRow(Texture sheet, int ROWS, int COLS, int row, int c0, int c1, float fps, Animation.PlayMode mode){
        TextureRegion[] arr = sliceRow(sheet, COLS, ROWS, row, c0, c1);
        Animation<TextureRegion> a = new Animation<>(1f/Math.max(1f,fps), new Array<>(arr));
        a.setPlayMode(mode);
        return a;
    }

    private TextureRegion[] sliceRow(Texture sheet, int cols, int rows, int rowIndex, int c0, int c1){
        int fw = sheet.getWidth()/cols, fh = sheet.getHeight()/rows;
        Array<TextureRegion> list = new Array<>();
        for (int c=c0;c<=c1;c++) list.add(new TextureRegion(sheet, c*fw, rowIndex*fh, fw, fh));
        return list.toArray(TextureRegion.class);
    }

    private Texture makeGroundTile(int w,int h){
        Pixmap pm=new Pixmap(w,h, Pixmap.Format.RGBA8888);
        pm.setColor(0.36f,0.33f,0.28f,1); pm.fill();
        pm.setColor(0.28f,0.25f,0.20f,1); pm.fillRectangle(0,0,w,h/3);
        pm.setColor(0.18f,0.16f,0.13f,1); for (int i=0;i<160;i++) pm.drawPixel((int)(Math.random()*w), (int)(Math.random()*h));
        pm.setColor(0.32f,0.29f,0.24f,1); for (int gy=8; gy<h; gy+=16) pm.drawLine(0,gy,w,gy);
        Texture t=new Texture(pm); pm.dispose(); return t;
    }

    private void buildHeroSpriteSets() {
        hugoSprites = buildHeroSet(
            hugoSwordFL, hugoSwordFR,
            hugoSwordFL, hugoSwordFR,
            hugoJumpFL, null,
            hugoSwordFL, hugoSwordFR
        );
        hugoGunSprites = buildHeroSet(
            hugoMitrailletteFL, hugoMitrailletteFR,
            hugoMitrailletteFL, hugoMitrailletteFR,
            hugoJumpMinigun != null ? hugoJumpMinigun : hugoJumpFL,
            null,
            hugoMitrailletteFL, hugoMitrailletteFR
        );

        alexisSprites = buildHeroSet(
            alexisSwordFL, alexisSwordFR,
            alexisSwordFL, alexisSwordFR,
            alexisJumpFL, alexisJumpFR,
            alexisSwordFL, alexisSwordFR
        );
    }

    private HeroSpriteSet buildHeroSet(Texture idleL, Texture idleR,
                                       Texture runL, Texture runR,
                                       Texture jumpL, Texture jumpR,
                                       Texture shootL, Texture shootR) {
        HeroSpriteSet.DirectionalAnimation idle = buildDirectional(idleL, idleR, 4f, Animation.PlayMode.LOOP);
        HeroSpriteSet.DirectionalAnimation run = buildDirectional(runL, runR, 8f, Animation.PlayMode.LOOP);
        HeroSpriteSet.DirectionalAnimation jump = buildDirectional(jumpL, jumpR, 6f, Animation.PlayMode.LOOP);
        HeroSpriteSet.DirectionalAnimation shoot = buildDirectional(shootL, shootR, 10f, Animation.PlayMode.NORMAL);
        return new HeroSpriteSet(idle, run, jump, shoot);
    }

    private HeroSpriteSet.DirectionalAnimation buildDirectional(Texture leftTexture, Texture rightTexture, float fps, Animation.PlayMode playMode) {
        Animation<TextureRegion> left = buildAnimation(leftTexture, fps, playMode);
        Animation<TextureRegion> right = buildAnimation(rightTexture, fps, playMode);
        if (left == null && right != null) {
            left = mirrorAnimation(right, fps, playMode);
        } else if (right == null && left != null) {
            right = mirrorAnimation(left, fps, playMode);
        }
        return new HeroSpriteSet.DirectionalAnimation(left, right);
    }

    private Animation<TextureRegion> mirrorAnimation(Animation<TextureRegion> source, float fps, Animation.PlayMode playMode) {
        if (source == null) return null;
        Object[] frames = source.getKeyFrames();
        TextureRegion[] flipped = new TextureRegion[frames.length];
        for (int i = 0; i < frames.length; i++) {
            TextureRegion original = (TextureRegion) frames[i];
            TextureRegion clone = new TextureRegion(original);
            clone.flip(true, false);
            flipped[i] = clone;
        }
        Animation<TextureRegion> animation = new Animation<>(1f / Math.max(1f, fps), flipped);
        animation.setPlayMode(playMode);
        return animation;
    }

    private Animation<TextureRegion> buildAnimation(Texture texture, float fps, Animation.PlayMode playMode) {
        if (texture == null) return null;
        SheetGrid grid = configFor(texture.getWidth(), texture.getHeight());
        SpriteSheet sheet = new SpriteSheet(texture, grid.columns, grid.rows);
        if (grid.useColumnStrip) {
            return sheet.animationFromColumn(grid.rowIndex, fps, playMode);
        }
        return sheet.animationFromRow(grid.rowIndex, fps, playMode);
    }

    private SheetGrid configFor(int width, int height) {
        if (width == 408 && height == 612) {
            return new SheetGrid(6, 1, 0);
        }
        if (width == 500 && height == 500) {
            return new SheetGrid(1, 5, 0, true);
        }
        if (height == 128) {
            if (width == 768) return new SheetGrid(6, 1, 0);
            if (width == 640) return new SheetGrid(5, 1, 0);
        }
        if (height == 64) {
            if (width == 400) return new SheetGrid(5, 1, 0);
            if (width == 320) return new SheetGrid(5, 1, 0);
        }
        return new SheetGrid(1, 1, 0);
    }

    private static class SheetGrid {
        final int columns;
        final int rows;
        final int rowIndex;
        final boolean useColumnStrip;

        SheetGrid(int columns, int rows, int rowIndex) {
            this(columns, rows, rowIndex, false);
        }

        SheetGrid(int columns, int rows, int rowIndex, boolean useColumnStrip) {
            this.columns = Math.max(1, columns);
            this.rows = Math.max(1, rows);
            this.rowIndex = Math.max(0, rowIndex);
            this.useColumnStrip = useColumnStrip;
        }
    }

    /**
     * Retrieves the sprite set matching the provided hero name.
     *
     * @param heroName either "Hugo" or "Alexis"
     * @return sprite set for the hero, falling back to the other one if needed
     */
    public HeroSpriteSet getHeroSpriteSet(String heroName) {
        if (heroName == null) {
            return hugoSprites != null ? hugoSprites : alexisSprites;
        }
        String normalized = heroName.trim().toLowerCase(Locale.ROOT);
        if (normalized.contains("alexis")) {
            return alexisSprites != null ? alexisSprites : hugoSprites;
        }
        if (normalized.contains("minigun") || normalized.contains("mitraillette") || normalized.contains("gatling")) {
            if (hugoGunSprites != null) return hugoGunSprites;
        }
        return hugoSprites != null ? hugoSprites : alexisSprites;
    }

    /**
     * Releases every texture created by this asset bundle.
     */
    public void dispose() {
        for (Texture t : new Texture[]{
            bg, groundTile, soldierSheet, zombieSheet, hearts,
            hugoSwordFL, hugoSwordFR, hugoJumpFL, hugoJumpFR,
            hugoMitrailletteFL, hugoMitrailletteFR, hugoPistoletFL, hugoPistoletFR,
            hugoJumpMinigun,
            alexisSwordFL, alexisSwordFR, alexisJumpFL, alexisJumpFR,
            alexisMitrailletteFL, alexisMitrailletteFR, alexisPistoletFL, alexisPistoletFR
        })
            if (t != null) t.dispose();
    }

    
    /**
     * Computes the world width that keeps the background aspect ratio.
     *
     * @param viewportH height of the camera viewport
     * @return scaled world width
     */
    public float computeWorldWidth(float viewportH){
        float scale = viewportH / bg.getHeight();
        return bg.getWidth()*scale;
    }
}
