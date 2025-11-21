package com.monzombie.game.screens;

import TestForMain.MainGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
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

/**
 * Simple screen that lets the player type a nickname before starting the campaign.
 */
public class NameEntryScreen extends InputAdapter implements Screen {

    private final MainGame game;
    private final int targetLevel;
    private final SpriteBatch batch;
    private final Viewport viewport;
    private Texture white1x1;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final StringBuilder nameBuilder = new StringBuilder();
    private final Rectangle rValidate = new Rectangle();
    private final Vector3 tmp = new Vector3();
    private boolean pressingValidate = false;
    private boolean requestedTextInput = false;

    public NameEntryScreen(MainGame game, int targetLevel) {
        this.game = game;
        this.targetLevel = targetLevel;
        this.batch = game.batch;
        this.viewport = new FitViewport(Constants.VW, Constants.VH);
        viewport.apply(true);
    }

    @Override
    public void show() {
        font = new BitmapFont();
        font.getData().setScale(2f);
        white1x1 = makeWhite();
        rValidate.set((Constants.VW - 360f) / 2f, 120f, 360f, 80f);
        Gdx.input.setInputProcessor(this);
        if (game.playerName != null) {
            nameBuilder.setLength(0);
            nameBuilder.append(game.playerName);
        }
        requestNativeInput();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.04f, 0.05f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            exitToLevelSelect();
            return;
        }

        viewport.unproject(tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        float mx = tmp.x, my = tmp.y;
        boolean justDown = Gdx.input.justTouched();
        boolean down = Gdx.input.isTouched();
        boolean justUp = !down && pressingValidate;
        if (justDown) pressingValidate = rValidate.contains(mx, my);
        if (justUp) {
            if (pressingValidate && rValidate.contains(mx, my)) confirmEntry();
            pressingValidate = false;
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        drawPanel();
        drawInputField();
        drawButton(rValidate, "VALIDER", rValidate.contains(mx, my), pressingValidate);
        batch.end();
    }

    private void drawPanel() {
        batch.setColor(0f, 0f, 0f, 0.45f);
        batch.draw(white1x1, 80f, 220f, Constants.VW - 160f, 280f);
        batch.setColor(Color.WHITE);
        String title = "Entre ton pseudo";
        layout.setText(font, title);
        font.draw(batch, title,
            (Constants.VW - layout.width) / 2f,
            460f);
        layout.setText(font, "(ENTER valide, ESC pour annuler)");
        font.getData().setScale(1.2f);
        font.draw(batch, layout,
            (Constants.VW - layout.width) / 2f,
            410f);
        font.getData().setScale(2f);
    }

    private void drawInputField() {
        float fieldW = Constants.VW - 200f;
        float fieldH = 80f;
        float fieldX = 100f;
        float fieldY = 300f;
        batch.setColor(0.15f, 0.17f, 0.22f, 0.95f);
        batch.draw(white1x1, fieldX, fieldY, fieldW, fieldH);
        batch.setColor(Color.WHITE);
        batch.draw(white1x1, fieldX, fieldY, fieldW, 4f);
        batch.draw(white1x1, fieldX, fieldY + fieldH - 4f, fieldW, 4f);
        batch.draw(white1x1, fieldX, fieldY, 4f, fieldH);
        batch.draw(white1x1, fieldX + fieldW - 4f, fieldY, 4f, fieldH);

        String display = nameBuilder.length() > 0 ? nameBuilder.toString() : "Tape ton nom";
        layout.setText(font, display);
        font.setColor(nameBuilder.length() > 0 ? Color.WHITE : Color.GRAY);
        font.draw(batch, display,
            fieldX + 20f,
            fieldY + fieldH / 2f + layout.height / 2f);
        font.setColor(Color.WHITE);
    }

    private void drawButton(Rectangle r, String text, boolean hover, boolean pressed) {
        Color fill = hover ? new Color(0.25f,0.48f,0.86f,0.95f) : new Color(0.16f,0.18f,0.22f,0.95f);
        batch.setColor(0f,0f,0f,0.35f);
        batch.draw(white1x1, r.x + 4, r.y - 4, r.width, r.height);
        float s = pressed ? 0.96f : 1f;
        float w = r.width * s;
        float h = r.height * s;
        float x = r.x + (r.width - w)/2f;
        float y = r.y + (r.height - h)/2f;
        batch.setColor(fill);
        batch.draw(white1x1, x, y, w, h);
        batch.setColor(Color.WHITE);
        layout.setText(font, text);
        font.draw(batch, text,
            x + (w - layout.width)/2f,
            y + (h + layout.height)/2f);
    }

    private void confirmEntry() {
        String trimmed = nameBuilder.toString().trim();
        if (trimmed.length() < 1) return;
        game.playerName = trimmed;
        proceedToNextScreen();
    }

    private void proceedToNextScreen() {
        if (targetLevel > 0) {
            game.setScreen(new CharacterSelectScreen(game, targetLevel));
        } else {
            game.setScreen(new LevelSelectScreen(game));
        }
    }

    private void exitToLevelSelect() {
        game.setScreen(new LevelSelectScreen(game));
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == 8) { // backspace
            if (nameBuilder.length() > 0) nameBuilder.deleteCharAt(nameBuilder.length() - 1);
            return true;
        }
        if (character == '\r' || character == '\n') {
            confirmEntry();
            return true;
        }
        if (character >= 32 && character < 127) {
            if (nameBuilder.length() < 18) {
                nameBuilder.append(character);
            }
            return true;
        }
        return false;
    }

    private void requestNativeInput() {
        if (requestedTextInput) return;
        requestedTextInput = true;
        Gdx.input.getTextInput(new Input.TextInputListener() {
            @Override
            public void input(String text) {
                if (text == null) return;
                nameBuilder.setLength(0);
                nameBuilder.append(text);
                confirmEntry();
            }

            @Override
            public void canceled() {}
        }, "Pseudo", nameBuilder.toString(), "Entrez votre nom");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (font != null) font.dispose();
        if (white1x1 != null) white1x1.dispose();
    }

    private Texture makeWhite() {
        Pixmap pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pm.setColor(1,1,1,1);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }
}
