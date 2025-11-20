package com.monzombie.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

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

    public HeroSpriteSet hugoSprites;
    public HeroSpriteSet alexisSprites;

    
    public Animation<TextureRegion> animWalk, animRun, animShot;
    public TextureRegion frameIdle;

    
    public Animation<TextureRegion> zombieWalk;

    
    public TextureRegion heartFull, heartEmpty;

    public void load() {
        bg = loadTexture("bunker.jpg");
        soldierSheet = loadTexture("sprite sheet 8-bit 2.png");
        zombieSheet  = loadTexture("zombie_sheet.png");
        hearts       = loadTexture("hearts.png");
        hugoSwordFL  = loadTexture("HugoSwordFL.png");
        hugoSwordFR  = loadTexture("HugoSwordFR.png");
        hugoJumpFL   = loadTexture("HugoJumpingFL.png");
        hugoJumpFR   = loadTexture("HugoJumpingFR.png");
        hugoMitrailletteFL = loadTexture("HugoMitrailletteFL.png");
        hugoMitrailletteFR = loadTexture("HugoMitrailletteFR.png");
        hugoPistoletFL = loadTexture("HugoPistoletFL.png");
        hugoPistoletFR = loadTexture("HugoPistoletFR.png");
        alexisSwordFL = loadTexture("AlexisSwordFL.png");
        alexisSwordFR = loadTexture("AlexisSwordFR.png");
        alexisJumpFL  = loadTexture("AlexisJumpingFL.png");
        alexisJumpFR  = loadTexture("AlexisJumpingFR.png");
        alexisMitrailletteFL = loadTexture("AlexisMitrailletteFL.png");
        alexisMitrailletteFR = loadTexture("AlexisMitrailletteFR.png");
        alexisPistoletFL = loadTexture("AlexisPistoletFL.png");
        alexisPistoletFR = loadTexture("AlexisPistoletFR.png");

        
        for (Texture t : new Texture[]{
            bg, soldierSheet, zombieSheet, hearts,
            hugoSwordFL, hugoSwordFR, hugoJumpFL, hugoJumpFR,
            hugoMitrailletteFL, hugoMitrailletteFR, hugoPistoletFL, hugoPistoletFR,
            alexisSwordFL, alexisSwordFR, alexisJumpFL, alexisJumpFR,
            alexisMitrailletteFL, alexisMitrailletteFR, alexisPistoletFL, alexisPistoletFR
        })
            if (t != null) t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        
        groundTile = makeGroundTile(64, 64);
        groundTile.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        buildPlayerAnims();
        buildZombieAnim();
        sliceHearts();

        buildHeroSpriteSets();
    }

    private Texture loadTexture(String name) {
        FileHandle fh = Gdx.files.internal(name);
        if (!fh.exists()) throw new RuntimeException("Asset introuvable: " + name + " (place-le dans core/assets/)");
        return new Texture(fh);
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
            hugoJumpFL, hugoJumpFR,
            hugoSwordFL, hugoSwordFR
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
        return new HeroSpriteSet.DirectionalAnimation(
            buildAnimation(leftTexture, fps, playMode),
            buildAnimation(rightTexture, fps, playMode)
        );
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

    public HeroSpriteSet getHeroSpriteSet(String heroName) {
        if ("Alexis".equalsIgnoreCase(heroName)) {
            return alexisSprites != null ? alexisSprites : hugoSprites;
        }
        return hugoSprites != null ? hugoSprites : alexisSprites;
    }

    public void dispose() {
        for (Texture t : new Texture[]{
            bg, groundTile, soldierSheet, zombieSheet, hearts,
            hugoSwordFL, hugoSwordFR, hugoJumpFL, hugoJumpFR,
            hugoMitrailletteFL, hugoMitrailletteFR, hugoPistoletFL, hugoPistoletFR,
            alexisSwordFL, alexisSwordFR, alexisJumpFL, alexisJumpFR,
            alexisMitrailletteFL, alexisMitrailletteFR, alexisPistoletFL, alexisPistoletFR
        })
            if (t != null) t.dispose();
    }

    
    public float computeWorldWidth(float viewportH){
        float scale = viewportH / bg.getHeight();
        return bg.getWidth()*scale;
    }
}
