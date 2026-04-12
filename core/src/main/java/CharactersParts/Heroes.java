package CharactersParts;

import BackgroundParts.GameScreen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Heroes extends Characters {

    GameScreen gp;
    KeyHandler kh;

    public Heroes(GameScreen gp, KeyHandler kh, String name, String sound, int numberOfHeart, int damage, int defense, int ennemiesAttack) {
        super(name, sound, numberOfHeart, damage, defense, ennemiesAttack);
        this.gp = gp;
        this.kh = kh;

        setDefaultValues();
    }


    public void setDefaultValues() {
        x=100;
        y=100;
        speed=4;
    }


    public void getHeroesImage(){
    try{

        upl = ImageIO.read(getClass().getResourceAsStream(""));
    } catch (RuntimeException e) {
        throw new RuntimeException(e);
    }
    }

    public void update() {

        if (kh.rightPressed == true) {
            y+= speed;
        }

        if (kh.leftPressed == true) {
            x -= speed ;
        }

        if (kh.spacePressed == true) {
            y-= speed;
        }
    }

    public void draw(Graphics2D g2) {

        g2.setColor(Color.white);

        // Need to verify with Manu how he named this variable
        // To call it here + verify if it's : protected/ private etc to access it

//        g2.fillRect(x, y, gp.tileSize, gp.tileSize);
    }




    // Need to override it after creating it in the branch later
    public void storyTellingStarter (String name){
        System.out.println("Hi " + name + "! I'm glad to see you again after what happened...");
        System.out.println(" We just woke up too... It already have been 3 years. " +
            "Have you heard of the rumor circulating around ?  " +
            "It seems that there are some survivors outside. We need to find them. " +
            "What ? You want to do go find them ? " +
            "It's dangerous ! " +
            "Okay, I will not go against your wishes. I let some weapon just outside when the apocalypse began " +
            "They might not be the best but I hope they will do the job. " +
            "Good luck on your quest !" +  name);
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
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

//    //public void finalChapter(int gamePosition){
//
//
//     int  x = playerPositionX;
//    int y = playPositionY;
//
//        if  (gamePosition(x , y )) {
//            System.out.println("I see some light ! I feel it, They will be there, the survivors !");
//        }
//    }

//    public void saySentence(String name, int n ) {
//
//        String randomHeroesSentence =
//            "My whole team count on me" +
//                "I trust the process" +
//                "I will find those survivors"
//            ;
//
//        StringBuilder sb = new StringBuilder(n);
//
//        for (int i = 0; i < n; i++) {
//            // generate a random number between
//            // 0 to AlphaNumericString variable length
//            int index
//                = (int)(randomHeroesSentence.length()
//                * Math.random());
//
//            sb.append(randomHeroesSentence.charAt(index));
//
//
//            if (name.equals("Alexis") || name.equals("Hugo")) {
//                for(int i=0; i < GameTime.length ; i++){
//
//                }
//        }
//        System.out.println("My whole team count on me");
//        System.out.println("I trust the process");
//        System.out.println("I will find those survivors");
//    }



}

