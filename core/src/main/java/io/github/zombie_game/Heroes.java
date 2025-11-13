package io.github.zombie_game;
import com.badlogic.gdx.math.Vector3;
import io.github.zombie_game.GameTime;
import java.lang.Math;

public class Heroes extends Characters {

    public Heroes(
        String name,
        String makeSound,
        int numberOfHeart,
        int damage,
        int defense,
        int ennemiesAttack,
        Vector3 position, Vector3 fall
        ) {
        super(name, makeSound, numberOfHeart, damage, defense, ennemiesAttack);
        
        };





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

