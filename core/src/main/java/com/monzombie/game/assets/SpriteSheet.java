package com.monzombie.game.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;





public class SpriteSheet {

    private final TextureRegion[][] cells;

    public SpriteSheet(Texture texture, int columns, int rows) {
        if (texture == null || columns <= 0 || rows <= 0) {
            cells = new TextureRegion[0][0];
            return;
        }
        int frameW = texture.getWidth() / columns;
        int frameH = texture.getHeight() / rows;
        cells = TextureRegion.split(texture, frameW, frameH);
    }

    public Animation<TextureRegion> animationFromRow(int rowIndex, float fps, Animation.PlayMode playMode) {
        if (cells.length == 0) return null;
        int row = clamp(rowIndex, 0, cells.length - 1);
        TextureRegion[] rowCells = cells[row];
        Array<TextureRegion> frames = new Array<>(rowCells.length);
        for (TextureRegion region : rowCells) {
            frames.add(new TextureRegion(region));
        }
        float frameDuration = 1f / Math.max(1f, fps);
        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(playMode);
        return animation;
    }

    public Animation<TextureRegion> animationFromColumn(int columnIndex, float fps, Animation.PlayMode playMode) {
        if (cells.length == 0) return null;
        int rows = cells.length;
        int columns = rows > 0 ? cells[0].length : 0;
        if (columns == 0) return null;
        int column = clamp(columnIndex, 0, columns - 1);
        Array<TextureRegion> frames = new Array<>(rows);
        for (TextureRegion[] rowCells : cells) {
            if (column >= rowCells.length) continue;
            frames.add(new TextureRegion(rowCells[column]));
        }
        float frameDuration = 1f / Math.max(1f, fps);
        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(playMode);
        return animation;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
