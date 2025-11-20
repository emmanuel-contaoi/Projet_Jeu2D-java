package com.monzombie.game.screens;

import TestForMain.MainGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.monzombie.game.util.Constants;

public class MenuScreen implements Screen {

    private final MainGame game;
    private final SpriteBatch batch;

    private final Viewport viewport;

    
    private Texture bg;              
    private float bgOffsetX = 0;     
    private float bgSpeed = 20f;     

    
    private Texture white1x1;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private final Rectangle rStart  = new Rectangle();
    private final Rectangle rOption = new Rectangle();
    private final Rectangle rExit   = new Rectangle();

    private final Vector3 tmp = new Vector3();
    private static final float BTN_W = 360f, BTN_H = 90f, BTN_GAP = 28f;
    private boolean pressingStart = false, pressingOption = false, pressingExit = false;

    public MenuScreen(MainGame game) {
        this.game = game;
        this.batch = game.batch;
        this.viewport = new FitViewport(Constants.VW, Constants.VH);
        viewport.apply(true);
    }

    @Override
    public void show() {
        font = new BitmapFont();
        font.getData().setScale(2.0f);
        white1x1 = makeWhite();

        
        if (Gdx.files.internal("menu_background.png").exists()) {
            bg = new Texture(Gdx.files.internal("menu_background.png"));
        }

        
        float x = (Constants.VW - BTN_W) / 2f;
        float top = Constants.VH / 2f + BTN_H + BTN_GAP;
        rStart.set(x, top, BTN_W, BTN_H);
        rOption.set(x, top - (BTN_H + BTN_GAP), BTN_W, BTN_H);
        rExit.set(x, top - 2*(BTN_H + BTN_GAP), BTN_W, BTN_H);
    }

    @Override
    public void render(float delta) {
        updateBackground(delta);
        Gdx.gl.glClearColor(0.06f,0.07f,0.08f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.unproject(tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        float mx = tmp.x, my = tmp.y;
        boolean justDown = Gdx.input.justTouched();
        boolean down = Gdx.input.isTouched();
        boolean justUp = !down && (pressingStart || pressingOption || pressingExit);

        if (justDown) {
            pressingStart  = rStart.contains(mx, my);
            pressingOption = rOption.contains(mx, my);
            pressingExit   = rExit.contains(mx, my);
        }
        if (justUp) {
            if (pressingStart  && rStart.contains(mx,my))  { game.setScreen(new LevelSelectScreen(game)); }
            if (pressingOption && rOption.contains(mx,my)) { game.setScreen(new LeaderboardScreen(game)); }
            if (pressingExit   && rExit.contains(mx,my))   { Gdx.app.exit(); }
            pressingStart = pressingOption = pressingExit = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new LevelSelectScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        drawAnimatedBackground(); 
        drawButton(rStart,  "START",  rStart.contains(mx,my),  pressingStart);
        drawButton(rOption, "SCORES", rOption.contains(mx,my), pressingOption);
        drawButton(rExit,   "EXIT",   rExit.contains(mx,my),   pressingExit);

        batch.end();
    }

    
    private void updateBackground(float delta) {
        if (bg == null) return;
        bgOffsetX += bgSpeed * delta;
        if (bgOffsetX > bg.getWidth()) {
            bgOffsetX -= bg.getWidth(); 
        }
    }

    private void drawAnimatedBackground() {
        if (bg == null) return;
        float scale = Constants.VH / (float) bg.getHeight();
        float tileW = bg.getWidth() * scale;
        float x = -bgOffsetX * scale;

        
        while (x < Constants.VW) {
            batch.draw(bg, x, 0, tileW, Constants.VH);
            x += tileW;
        }
    }

    
    private void drawButton(Rectangle r, String text, boolean hover, boolean pressed) {
        Color base = new Color(0.16f,0.18f,0.22f,0.95f);
        Color hoverCol = new Color(0.24f,0.49f,0.86f,0.95f);
        Color fill = hover ? hoverCol : base;

        batch.setColor(0,0,0,0.35f);
        batch.draw(white1x1, r.x+4, r.y-4, r.width, r.height);

        float s = pressed ? 0.96f : 1f;
        float w = r.width * s;
        float h = r.height * s;
        float x = r.x + (r.width - w)/2f;
        float y = r.y + (r.height - h)/2f;

        batch.setColor(fill);
        batch.draw(white1x1, x, y, w, h);

        batch.setColor(0.92f,0.94f,0.98f,1f);
        float b = 4f;
        batch.draw(white1x1, x, y, w, b);
        batch.draw(white1x1, x, y+h-b, w, b);
        batch.draw(white1x1, x, y, b, h);
        batch.draw(white1x1, x+w-b, y, b, h);

        batch.setColor(Color.WHITE);
        layout.setText(font, text);
        font.draw(batch, layout, x + (w - layout.width)/2f, y + (h + layout.height)/2f);
    }

    private Texture makeWhite() {
        Pixmap pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pm.setColor(1,1,1,1); pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        if (bg != null) bg.dispose();
        if (white1x1 != null) white1x1.dispose();
        if (font != null) font.dispose();
    }
}
