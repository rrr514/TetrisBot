package assignment;

import java.awt.Point;
import java.util.*;

import assignment.Board.Action;

/**
 * A Lame Brain implementation for JTetris; tries all possible places to put the
 * piece (but ignoring rotations, becaLameBrainuse we're lame), trying to minimize the
 * total height of pieces on the board.
 */
public class GigaBrain implements Brain {
    int noRotateIndex; //index for firstMoves; all indexes before this dont need rotations
    private ArrayList<Board> options;
    private ArrayList<Board.Action> firstMoves;

    /**
     * Decide what the next move should be based on the state of the board.
     */
    public Board.Action nextMove(Board currentBoard) {
        // Fill the out options array with versions of the new Board
        options = new ArrayList<>();
        firstMoves = new ArrayList<>();
        enumerateOptions(currentBoard);
        // if(currentBoard.getLastResult()== Board.Result.OUT_BOUNDS){
        //     countPieces++;
        //     return Board.Action.DROP;
        // }
        // if(countRounds>countPieces*4){
        //     countPieces++;
        //     countRounds/=4;
        //     return Board.Action.DROP;
        // }
        double best = -Double.MAX_VALUE;
        int bestIndex = 0;

        // Check all of the options and get the one with the highest score
        for (int i = 0; i < options.size(); i++) {
            double score = scoreBoard(options.get(i));
            if (score > best) {
                best = score;
                bestIndex = i;
            }
        }

        // System.out.println("Best Score: " + best);

        // if(firstMoves.get(bestIndex)== Board.Action.DROP){
        //     countPieces++;
        // }
        // countRounds++;
        // We want to return the first move on the way to the best Board
        if(bestIndex >= noRotateIndex){
            System.out.println("Rotating");
            return Action.COUNTERCLOCKWISE;
        }
        System.out.println(firstMoves.get(bestIndex));
        return firstMoves.get(bestIndex);
    }

    /**
     * Test all of the places we can put the current Piece.
     * Since this is just a Lame Brain, we aren't going to do smart
     * things like rotating pieces.
     */
    private void enumerateOptions(Board currentBoard) {
        // We can always drop our current Piece
        options.add(currentBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DROP);
        //chcek all options for all rotations
        for(int i = 0;i < 4;i++){
            translateBoard(currentBoard);
            if(i == 0) noRotateIndex = firstMoves.size();
            currentBoard = currentBoard.testMove(Action.COUNTERCLOCKWISE);
        }
        
    }

    private void translateBoard(Board currentBoard){
        // Now we'll add all the places to the left we can DROP
        Board left = currentBoard.testMove(Board.Action.LEFT);
        while (left.getLastResult() == Board.Result.SUCCESS) {
            options.add(left.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.LEFT);
            left.move(Board.Action.LEFT);
        }

        // And then the same thing to the right
        Board right = currentBoard.testMove(Board.Action.RIGHT);
        while (right.getLastResult() == Board.Result.SUCCESS) {
            options.add(right.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.RIGHT);
            right.move(Board.Action.RIGHT);
        }
    }

    /**
     * Since we're trying to avoid building too high,
     * we're going to give higher scores to Boards with
     * MaxHeights close to 0.
     */
    private double scoreBoard(Board newBoard) {
        int aggregateHeight = 0;
        int completeLines = 0;
        int holes = 0;
        int bumpiness = 0;
        
        //aggregate height
        for(int i = 0;i < newBoard.getWidth();i++){
            aggregateHeight += newBoard.getColumnHeight(i);
        }

        //completed lines
        for(int j = newBoard.getHeight()-1;j >= 0;j--){
            if(newBoard.getRowWidth(j) == newBoard.getWidth()){
                completeLines++;
            }
        }

        //holes
        Piece curPiece = newBoard.getCurrentPiece();
        Point pos = newBoard.getCurrentPiecePosition();
        Point[] body = curPiece.getBody();
        for(Point p: body){
            Point loc = new Point(pos.x + p.x, pos.y + p.y);
            //check cell below
            if(loc.y-1 >= 0 && newBoard.getGrid(loc.x, loc.y-1) == null){
                holes++;
            }
        }

        //bumpiness
        for(int i = 0;i < newBoard.getWidth()-1;i++){
            bumpiness += Math.abs(newBoard.getColumnHeight(i) - newBoard.getColumnHeight(i+1));
        }

        //multipliers for each score
        double a = -0.510066;
        double b = 0.760666;
        double c = -0.35663;
        double d = -0.184483;
        double score = (a * aggregateHeight) + (b * completeLines) + (c * holes) + (d * bumpiness);
        return score;
    }

}