package assignment;

import java.awt.*;
import java.util.Arrays;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 * 
 * All operations on a TetrisPiece should be constant time, except for it's
 * initial construction. This means that rotations should also be fast - calling
 * clockwisePiece() and counterclockwisePiece() should be constant time! You may
 * need to do precomputation in the constructor to make this possible.
 */
public final class TetrisPiece implements Piece {
    Point [] points;
    PieceType type;
    int index;
    private Point [][] getSquare = new Point[][] {
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)}
    };
    private Point [][] getT = new Point[][] {
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 1)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 0)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 1)}
    };
    private Point [][] getStick = new Point[][] {
            {new Point(0, 2), new Point(1, 2), new Point(2, 2), new Point(3, 2)},
            {new Point(2, 0), new Point(2, 1), new Point(2, 2), new Point(2, 3)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}
    };
    private Point [][] getLeftL = new Point[][] {
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0)}
    };
    private Point [][] getRightL = new Point[][] {
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)}
    };
    private Point [][] getLeftDog = new Point[][] {
            {new Point(0, 2), new Point(1, 2), new Point(1, 1), new Point(2, 1)},
            {new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
            {new Point(0, 1), new Point(1, 1), new Point(1, 0), new Point(2, 0)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)}
    };
    private Point [][] getRightDog = new Point[][] {
            {new Point(0, 1), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
            {new Point(1, 2), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
            {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
            {new Point(0, 2), new Point(0, 1), new Point(1, 1), new Point(1, 0)}
    };
    private Point [] getRotation(PieceType type, int rotation){
        switch (type){
            case T: return getT[rotation];
            case SQUARE: return getSquare[rotation];
            case STICK: return getStick[rotation];
            case LEFT_L: return getLeftL[rotation];
            case RIGHT_L: return getRightL[rotation];
            case LEFT_DOG: return getLeftDog[rotation];
            case RIGHT_DOG: return getRightDog[rotation];
            default: return null;
        }
    }
    /**
     * Construct a tetris piece of the given type. The piece should be in it's spawn orientation,
     * i.e., a rotation index of 0.
     * 
     * You may freely add additional constructors, but please leave this one - it is used both in
     * the runner code and testing code.
     */
    public TetrisPiece(PieceType type) {
        this.type = type;
        points = type.getSpawnBody();
        index = 0;
    }
    public TetrisPiece(PieceType type, int rotationIndex, Point [] points) {
        this.type = type;
        index = rotationIndex;
        this.points = points;
    }

    @Override
    public PieceType getType() {
        return type;
    }

    @Override
    public int getRotationIndex() {
        return index;
    }
    private int rotate(int index, int direction){
        return (((index+direction) % 4) + 4) % 4;
    }

    @Override
    public Piece clockwisePiece() {
        return new TetrisPiece(type, rotate(index, 1), getRotation(type, rotate(index, 1)));
    }

    @Override
    public Piece counterclockwisePiece() {
        return new TetrisPiece(type,  rotate(index, -1), getRotation(type, rotate(index, -1)));
    }

    @Override
    public int getWidth() {
        return (int)type.getBoundingBox().getWidth();
    }

    @Override
    public int getHeight() {
        return (int)type.getBoundingBox().getHeight();
    }

    @Override
    public Point[] getBody() {
        return points;
    }

    @Override
    public int[] getSkirt() {
        if(type == PieceType.STICK){
            int [] skirt = new int[4];
            Arrays.fill(skirt, Integer.MAX_VALUE);
            for(Point p: points){
                skirt[p.x] = Math.min(skirt[p.x], p.y);
            }
            return skirt;
        }else if (type == PieceType.SQUARE){
            int [] skirt = new int[2];
            Arrays.fill(skirt, Integer.MAX_VALUE);
            for(Point p: points){
                skirt[p.x] = Math.min(skirt[p.x], p.y);
            }
            return skirt;
        }else{
            int [] skirt = new int[3];
            Arrays.fill(skirt, Integer.MAX_VALUE);
            for(Point p: points){
                skirt[p.x] = Math.min(skirt[p.x], p.y);
            }
            return skirt;
        }
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;
        return otherPiece.type==this.type && otherPiece.index==this.index;
    }
}
