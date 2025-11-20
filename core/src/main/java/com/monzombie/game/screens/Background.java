package com.monzombie.game.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Background {

    private final Texture texture;
    private final Array<Rectangle> colliders;   // sol + obstacles

    // taille du monde (à adapter à ton jeu)
    public static final float WORLD_WIDTH  = 1280f;
    public static final float WORLD_HEIGHT = 720f;

    public Background() {
        texture = new Texture("map_1_bunker_sol.png");
        colliders = new Array<>();

        // --- 1) SOL GÉNÉRAL ---
        // ici je considère que le sol est en bas de l’image
        float groundHeight = 64f; // épaisseur du sol (en unités monde)
        colliders.add(new Rectangle(
            0,
            0,                        // sol à Y = 0
            WORLD_WIDTH,
            groundHeight
        ));

        // --- 2) OBSTACLES / PLATEFORMES ---
        // Tu DOIS ajuster les positions X/Y et tailles
        // en fonction de ta vraie image (caisses, barils, plateformes…)

        // Exemple : une caisse à gauche
        colliders.add(new Rectangle(
            250f,            // x
            groundHeight,    // pose sur le sol
            64f,             // largeur
            64f              // hauteur
        ));

        // Exemple : baril au milieu
        colliders.add(new Rectangle(
            600f,
            groundHeight,
            48f,
            80f
        ));

        // Exemple : petite plateforme en hauteur
        colliders.add(new Rectangle(
            900f,
            groundHeight + 120f,
            160f,
            32f
        ));
    }

    public void render(SpriteBatch batch) {
        // on dessine l’image en bas de l’écran
        batch.draw(texture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
    }

    public Array<Rectangle> getColliders() {
        return colliders;
    }

    public void dispose() {
        texture.dispose();
    }
}
