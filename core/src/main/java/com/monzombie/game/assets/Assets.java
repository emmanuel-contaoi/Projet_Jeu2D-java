package com.monzombie.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Assets {

    // Textures
    public Texture bg;                // bunker.jpg
    public Texture groundTile;        // générée
    public Texture soldierSheet;      // sprite sheet 8-bit 2.png
    public Texture zombieSheet;       // zombie_sheet.png
    public Texture hearts;            // hearts.png
    public Texture hugoSwordFL;
    public Texture hugoSwordFR;
    public Texture hugoJumpFL;
    public Texture hugoJumpFR;
    public Texture alexisSwordFL;
    public Texture alexisSwordFR;
    public Texture alexisJumpFL;
    public Texture alexisJumpFR;

    // simples "animations" des héros (liste de frames)
    public Array<TextureRegion> hugoGroundL = new Array<>();
    public Array<TextureRegion> hugoGroundR = new Array<>();
    public Array<TextureRegion> hugoJumpL = new Array<>();
    public Array<TextureRegion> hugoJumpR = new Array<>();

    public Array<TextureRegion> alexisGroundL = new Array<>();
    public Array<TextureRegion> alexisGroundR = new Array<>();
    public Array<TextureRegion> alexisJumpL = new Array<>();
    public Array<TextureRegion> alexisJumpR = new Array<>();

    // Animations player
    public Animation<TextureRegion> animWalk, animRun, animShot;
    public TextureRegion frameIdle;

    // Animations zombie
    public Animation<TextureRegion> zombieWalk;

    // Hearts
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
        alexisSwordFL = loadTexture("AlexisSwordFL.png");
        alexisSwordFR = loadTexture("AlexisSwordFR.png");
        alexisJumpFL  = loadTexture("AlexisJumpingFL.png");
        alexisJumpFR  = loadTexture("AlexisJumpingFR.png");

        // filtres pixel
        for (Texture t : new Texture[]{
            bg, soldierSheet, zombieSheet, hearts,
            hugoSwordFL, hugoSwordFR, hugoJumpFL, hugoJumpFR,
            alexisSwordFL, alexisSwordFR, alexisJumpFL, alexisJumpFR
        })
            if (t != null) t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // ground tile
        groundTile = makeGroundTile(64, 64);
        groundTile.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        buildPlayerAnims();
        buildZombieAnim();
        sliceHearts();

        buildHeroFrames();
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
        // ton image : 5 colonnes x 3 lignes
        int COLS=5, ROWS=3;
        int hw = hearts.getWidth()/COLS;
        int hh = hearts.getHeight()/ROWS;
        heartFull  = new TextureRegion(hearts, 0*hw, 0*hh, hw, hh); // plein (première cellule)
        heartEmpty = new TextureRegion(hearts, 2*hw, 1*hh, hw, hh); // vide (deuxième ligne, troisième col)
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

    /**
     * Met chaque image simple dans une liste, pour faire une "animation" frame par frame.
     * Si un jour tu ajoutes plus d'images, tu peux juste les ajouter dans ces listes.
     */
    private void buildHeroFrames() {
        addFrame(hugoGroundL, hugoSwordFL);
        addFrame(hugoGroundR, hugoSwordFR);
        addFrame(hugoJumpL, hugoJumpFL);
        addFrame(hugoJumpR, hugoJumpFR);

        addFrame(alexisGroundL, alexisSwordFL);
        addFrame(alexisGroundR, alexisSwordFR);
        addFrame(alexisJumpL, alexisJumpFL);
        addFrame(alexisJumpR, alexisJumpFR);
    }

    private void addFrame(Array<TextureRegion> list, Texture tex) {
        if (tex == null) return;
        list.add(new TextureRegion(tex));
    }

    public void dispose() {
        for (Texture t : new Texture[]{
            bg, groundTile, soldierSheet, zombieSheet, hearts,
            hugoSwordFL, hugoSwordFR, hugoJumpFL, hugoJumpFR,
            alexisSwordFL, alexisSwordFR, alexisJumpFL, alexisJumpFR
        })
            if (t != null) t.dispose();
    }

    // util pour longueur du monde en fonction du bg
    public float computeWorldWidth(float viewportH){
        float scale = viewportH / bg.getHeight();
        return bg.getWidth()*scale*3f;
    }
}
