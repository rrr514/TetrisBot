package assignment;

import javax.swing.*;
import javax.swing.plaf.IconUIResource;

public class JBrainTetris extends JTetris {
    GigaBrain brain = new GigaBrain();
    // LameBrain brain = new LameBrain();

    public static void main(String[] args){
//        int min = 100000;
//        int max = 0;
//        int avg = 0;
//        for(int i=0;i<1000;i++){
//            System.out.println(i);
//            JBrainTetris j = new JBrainTetris();
//            createGUI(j);
//            j.startGame();
//            while(j.gameOn){
//                //System.out.println("hi");
//                j.speed = new JSlider(0, 400, 400);
//            }
//            avg+=j.count;
//            min = Math.min(j.count, min);
//            max = Math.max(j.count, max);
//        }
//        System.out.println("min: " + min);
//        System.out.println("max: " + max);
//        System.out.println("avg: " + (avg/1000));
        createGUI(new JBrainTetris());
    }

    JBrainTetris(){
        super();
    }

    public void tick(Board.Action verb){
        verb = brain.nextMove(board);
        super.tick(verb);
        //super.tick(Board.Action.DROP);
    }
}