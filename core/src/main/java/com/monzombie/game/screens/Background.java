package com.monzombie.game.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Background {

    private final Texture texture;
    private final Array<Rectangle> colliders;   

    
    public static final float WORLD_WIDTH  = 1280f;
    public static final float WORLD_HEIGHT = 720f;

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

    public void render(SpriteBatch batch) {
        
        batch.draw(texture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
    }

    public Array<Rectangle> getColliders() {
        return colliders;
    }

    public void dispose() {
        texture.dispose();
    }
}
