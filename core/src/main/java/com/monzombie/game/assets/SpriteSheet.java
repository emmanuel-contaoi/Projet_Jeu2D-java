package com.monzombie.game.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Convenience wrapper around a texture that exposes row or column based animations.
 */
public class SpriteSheet {

    private final TextureRegion[][] cells;

    /**
     * Splits the supplied texture into evenly sized frames.
     *
     * @param texture sprite sheet texture
     * @param columns number of horizontal slices
     * @param rows number of vertical slices
     */
    public SpriteSheet(Texture texture, int columns, int rows) {
        if (texture == null || columns <= 0 || rows <= 0) {
            cells = new TextureRegion[0][0];
            return;
        }
        int frameW = texture.getWidth() / columns;
        int frameH = texture.getHeight() / rows;
        cells = TextureRegion.split(texture, frameW, frameH);
    }

    /**
     * Creates an animation using every frame of a single row.
     *
     * @param rowIndex index of the row to sample
     * @param fps target frames per second
     * @param playMode LibGDX play mode
     * @return animation or null when the sheet is invalid
     */
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

    /**
     * Creates an animation made from one column, useful for tall strips.
     *
     * @param columnIndex index of the column to sample
     * @param fps target frames per second
     * @param playMode LibGDX play mode
     * @return animation or null when the sheet is invalid
     */
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
