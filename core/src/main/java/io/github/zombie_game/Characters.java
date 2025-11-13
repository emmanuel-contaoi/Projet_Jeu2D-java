package io.github.zombie_game;

import com.badlogic.gdx.math.Vector3;

public abstract class Characters implements Move, Fight {

    // Trying to add some parameters see if it's private or public
    // How to be sure that one is private and the other should be public for example ?
    protected String name;
    protected String makeSound;

    private int numberOfHeart = 0 ;
    //Making defeat as a constant ?
    // It's better to declare it here =3 or in the constructor ?
    // Best practice ?


    // private int defeat ;

    //redundant because already initialised ?
    private int damage = 0;
    private int defense = 0;
    private int ennemiesAttack = 1;
    private Vector3 position, fall ;


    // Making the constructor
    public Characters(String name, String sound, int numberOfHeart, int damage, int defense, int ennemiesAttack) {
        this.name = name;
        this.makeSound = sound;

        //Putting the parameter inside the constructor but declaring it inside the "()" ?
        this.numberOfHeart = 5;
        // this.defeat = defeat;
        this.damage = damage;
        this.defense = defense;
        this.ennemiesAttack = ennemiesAttack;

    }

    // Accessing the private attribute


    public String getName() {return name;}

    public String getMakeSound() { return makeSound; }

    public  int getNumberOfHeart() {return numberOfHeart; }

    // Not obligated to use int ? Making it in a parameter
    public void getDefeat(int defeat) {

        if (defeat == 0){
            System.out.println(" Game Over :( ");
            System.out.println("Would you like to try again ?");

            //Putting exit to exit game when characters is defeated ?
            System.exit(0);
        }
    }

    public int getDamage(int attack) {return damage; }

    public int getDefense() { return defense; }

    public int getEnnemiesAttack() {return ennemiesAttack;}

    // Initializing the setter to be able to modify the parameter as time goes by
    // I mean if a parameter need to be modified depending on an external situation

    public void setDamage(int newDamage) {
        this.damage = newDamage;
    }
    public void setDefense(int newDefense) {
        this.defense = newDefense;
    }

    public void setNumberOfHeart(int newNumberOfHeart) {
        this.numberOfHeart = newNumberOfHeart;
    }

    public void setMakeSound(String newSound) {
        this.makeSound = newSound;
    }

    public void setEnnemiesAttack(int newEnnemiesAttack) {
        this.ennemiesAttack = newEnnemiesAttack;
    }
}
