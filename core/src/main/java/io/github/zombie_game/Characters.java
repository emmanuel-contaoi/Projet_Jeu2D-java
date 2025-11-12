package io.github.zombie_game;

public abstract class Characters {

    // Trying to add some parameters see if it's private or public
    // How to be sure that one is private and the other should be public for example ?
    protected String name;
    protected String makeSound;

    private int numberOfHeart  ;
    //Making defeat as a constant ?
    // It's better to declare it here =3 or in the constructor ?
    // Best practice ?


    // private int defeat ;
    private int damage = 3;
    private int defense = 5;


    // Making the constructor
    public Characters(String name, String sound, int numberOfHeart, int damage, int defense) {
        this.name = name;
        this.makeSound = sound;

        //Putting the parameter inside the constructor but declaring it inside the "()" ?
        this.numberOfHeart = 5;
       // this.defeat = defeat;
        this.damage = damage;
        this.defense = defense;
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

    public int getDamage() {return damage; }

    public int getDefense() { return defense; }

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


}
