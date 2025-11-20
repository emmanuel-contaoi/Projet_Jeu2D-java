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

        // filtres pixel
        for (Texture t : new Texture[]{bg, soldierSheet, zombieSheet, hearts})
            if (t != null) t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // ground tile
        groundTile = makeGroundTile(64, 64);
        groundTile.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        buildPlayerAnims();
        buildZombieAnim();
        sliceHearts();
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

    public void dispose() {
        for (Texture t : new Texture[]{bg, groundTile, soldierSheet, zombieSheet, hearts})
            if (t != null) t.dispose();
    }

    // util pour longueur du monde en fonction du bg
    public float computeWorldWidth(float viewportH){
        float scale = viewportH / bg.getHeight();
        return bg.getWidth()*scale*3f;
    }
}
