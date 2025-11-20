package com.monzombie.game.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Simple background layer that also defines a few rectangular colliders.
 */
public class Background {

    private final Texture texture;
    private final Array<Rectangle> colliders;   

    
    public static final float WORLD_WIDTH  = 1280f;
    public static final float WORLD_HEIGHT = 720f;

    /**
     * Loads the bunker texture and defines hardcoded collision rectangles.
     */
    public Background() {
        texture = new Texture("map_1_bunker_sol.png");
        colliders = new Array<>();

        
        
        float groundHeight = 64f; 
        colliders.add(new Rectangle(
            0,
            0,                        
            WORLD_WIDTH,
            groundHeight
        ));

        
        
        

        
        colliders.add(new Rectangle(
            250f,            
            groundHeight,    
            64f,             
            64f              
        ));

        
        colliders.add(new Rectangle(
            600f,
            groundHeight,
            48f,
            80f
        ));

        
        colliders.add(new Rectangle(
            900f,
            groundHeight + 120f,
            160f,
            32f
        ));
    }

    /**
     * Draws the background texture stretched to the world bounds.
     *
     * @param batch sprite batch already configured with the camera
     */
    public void render(SpriteBatch batch) {
        
        batch.draw(texture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
    }

    /**
     * Provides the rectangles that should be treated as solid geometry.
     *
     * @return immutable reference to the collider list
     */
    public Array<Rectangle> getColliders() {
        return colliders;
    }

    /**
     * Disposes the background texture when the screen closes.
     */
    public void dispose() {
        texture.dispose();
    }
}
