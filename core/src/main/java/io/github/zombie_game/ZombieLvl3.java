package io.github.zombie_game;

public class ZombieLvl3 extends Zombies{
    public ZombieLvl3(String name, String sound, int numberOfHeart, int damage, int defense, int ennemiesAttack) {
        super(name, sound, numberOfHeart, damage, defense, ennemiesAttack);
    }


    @Override
    public void fightWithSword() {

    }

    @Override
    public void fightWithGun() {

    }

    @Override
    public void fightWithMachineGun() {

    }

    @Override
    public void moveLeft() {

    }

    @Override
    public void moveRight() {

    }

    @Override
    public void stayStill() {

    }

    @Override
    public void jump() {

    }

    @Override
    public void jumpLeft() {

    }

    @Override
    public void jumpRight() {

    }

    public static class Gun extends Weapon {
    }

    public static class Knife extends Weapon {
    }
}
