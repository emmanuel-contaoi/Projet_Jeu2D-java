package io.github.zombie_game;

public abstract class Characters {

    // Trying to add some parameters see if it's private or public
    // How to be sure that one is private and the other should be public for example ?
    public String name;
    public String makeSound;

    private final int numberOfHeart ;
    private final int defeat = (numberOfHeart = 0);
    private int damage;
    private int defense;


    // Making the constructor
    public Characters(String name, String sound, int damage, int defense) {
        this.name = name;
        this.makeSound = sound;
        this.damage = damage;
        this.defense = defense;
    }

    // Accessing the private attribute
    public int getNumberOfHeart() {
        return numberOfHeart;
    }

    public int getDefeat() {
        return defeat;
    }

    public int getDamage() {
        return damage;
    }

    public int getDefense() {
        return defense;
    }

    // Initializing the setter to be able to modify the parameter as time goes by
    // I mean if a parameter need to be modified depending on an external situation

    public void setDamage(int damage) {
        this.damage = damage;
    }
    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setNumberOfHeart(int numberOfHeart) {
    }

    public void setMakeSound(String makeSound) {
        this.makeSound = makeSound;
    }


}
