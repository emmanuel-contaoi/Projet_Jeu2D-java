package io.github.zombie_game;

import com.badlogic.gdx.math.Vector3;

public abstract class Zombies extends Characters{

    public Zombies(String name, String sound, int numberOfHeart, int damage, int defense, int ennemiesAttack) {
        super(name, sound, numberOfHeart, damage, defense, ennemiesAttack);
    }


}
