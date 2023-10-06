package assignment;

import java.awt.*;
import java.util.LinkedList;

/**
 * Represents a Tetris board -- essentially a 2-d grid of piece types (or
 * nulls). Supports
 * tetris pieces and row clearing. Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2-d board.
 */
public final class TetrisBoard implements Board {
    int width;
    int height;
    LinkedList<Piece>[] board;
    Piece curPiece;
    Point position;
    int rowsCleared;
    Action lastAction;
    Result lastResult;


    // JTetris will use this constructor
    public TetrisBoard(int width, int height) {
        if(width<0 || height<0){
            throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
        board = new LinkedList[width];
        for(int i=0;i<width;i++){
            board[i] = new LinkedList<Piece>();
            for(int j=0;j<height;j++){
                board[i].add(null);
            }
        }
        rowsCleared = 0;
        lastAction = Action.NOTHING;
        lastResult = Result.NO_PIECE;
       // board[0].set(height-1, new TetrisPiece(Piece.PieceType.T));
    }

    public boolean checkCollisions(Point p, Action act) {
        //Point pAbs = getAbsolutePosition(p, position);
        if(p.x<0 || p.y<0 || p.x>=width || p.y>=height){
            return true;
        }
        if (p.y == 0) {
            return true;
        }
        switch (act){
            case LEFT: return p.x==0 || board[p.x-1].get(p.y)!=null;
            case RIGHT: return p.x==width-1 || board[p.x+1].get(p.y)!=null;
            case DOWN: return board[p.x].get(p.y-1)!=null;
            case CLOCKWISE: case COUNTERCLOCKWISE: return board[p.x].get(p.y)!=null;
        }
        return false;
    }

    //TODO: Set last Action  + Last Result  + OOB Glitch + Piece Deletion Glitch
    private void clearRows(){
        for(int i=0;i<height;i++){
            if(getRowWidth(i)==width){
                //System.out.println("hi");
                for(int j=0;j<width;j++){
                    board[j].remove(i);
                    board[j].add(null);
                }
                rowsCleared++;
            }
        }
    }
    @Override
    public Result move(Action act) {
        lastAction = act;
        if(curPiece == null){
            lastResult = Result.NO_PIECE;
            return Result.NO_PIECE;
        }
        switch (act) {
            case LEFT:
                //if collision or at edge of board, return OUT_BOUNDS result
                for (Point p1 : curPiece.getBody()) {
                    Point p1Abs = getAbsolutePosition(p1, position);
                    if (checkCollisions(p1Abs, act)) {
                        lastResult = Result.OUT_BOUNDS;
                        clearRows();
                        return Result.OUT_BOUNDS;
                    }
                }
                //otherwise move the piece
                position.move(position.x - 1, position.y);
                lastResult = Result.SUCCESS;
                clearRows();
                return Result.SUCCESS;
            case RIGHT:
                for (Point p : curPiece.getBody()) {
                    for (Point p1 : curPiece.getBody()) {
                        Point p1Abs = getAbsolutePosition(p1, position);
                        if (checkCollisions(p1Abs, act)) {
                            lastResult = Result.OUT_BOUNDS;
                            clearRows();
                            return Result.OUT_BOUNDS;
                        }
                    }
                }
                position.move(position.x + 1, position.y);
                lastResult = Result.SUCCESS;
                clearRows();
                return Result.SUCCESS;
            case DOWN:
                for (Point p : curPiece.getBody()) {
                    Point p1Abs = getAbsolutePosition(p, position);
                    if (checkCollisions(p1Abs, act)) {
                        for (Point p1 : curPiece.getBody()) {
                            p1Abs = getAbsolutePosition(p1, position);
                            board[p1Abs.x].set(p1Abs.y, curPiece);
                        }
                        lastResult = Result.PLACE;
                        clearRows();
                        return Result.PLACE;
                    }
                }
                position.move(position.x, position.y - 1);
                lastResult = Result.SUCCESS;
                clearRows();
                return Result.SUCCESS;
            case CLOCKWISE:
                Piece clockwisePiece = curPiece.clockwisePiece();
                for(Point p: clockwisePiece.getBody()){
                    Point pAbs = getAbsolutePosition(p, position);
                    if(checkCollisions(pAbs, act)){
                        //check wall kicks;
                        //if wall kicks -> reset position
                        //if wall kicks false ->
                        if(curPiece.getType() == Piece.PieceType.STICK){
                            boolean noCollisions;
                            for(Point wallKick: Piece.I_CLOCKWISE_WALL_KICKS[curPiece.getRotationIndex()]){
                                noCollisions = true;
                                Point wkPos = new Point(position.x + wallKick.x, position.y + wallKick.y);
                                for(Point p2: clockwisePiece.getBody()){
                                    Point p2Abs = getAbsolutePosition(p2, wkPos);
                                    if(checkCollisions(p2Abs, act)){
                                        noCollisions = false;
                                        break;
                                    }
                                }
                                if(noCollisions){
                                    curPiece = clockwisePiece;
                                    position.move(wkPos.x, wkPos.y);
                                    lastResult = Result.SUCCESS;
                                    clearRows();
                                    return Result.SUCCESS;
                                }
                            }
                        }else{
                            boolean noCollisions;
                            for(Point wallKick: Piece.NORMAL_CLOCKWISE_WALL_KICKS[curPiece.getRotationIndex()]){
                                noCollisions = true;
                                Point wkPos = new Point(position.x + wallKick.x, position.y + wallKick.y);
                                for(Point p2: clockwisePiece.getBody()){
                                    Point p2Abs = getAbsolutePosition(p2, wkPos);
                                    if(checkCollisions(p2Abs, act)){
                                        noCollisions = false;
                                        break;
                                    }
                                }
                                if(noCollisions){
                                    curPiece = clockwisePiece;
                                    position.move(wkPos.x, wkPos.y);
                                    lastResult = Result.SUCCESS;
                                    clearRows();
                                    return Result.SUCCESS;
                                }
                            }
                        }
                        lastResult = Result.OUT_BOUNDS;
                        clearRows();
                        return Result.OUT_BOUNDS;
                    }
                }
                curPiece = clockwisePiece;
                lastResult = Result.SUCCESS;
                clearRows();
                return Result.SUCCESS;
            case COUNTERCLOCKWISE:
                Piece counterPiece = curPiece.counterclockwisePiece();
                for(Point p: counterPiece.getBody()){
                    Point pAbs = getAbsolutePosition(p, position);
                    if(checkCollisions(pAbs, act)){
                        //check wall kicks;
                        //if wall kicks -> reset position
                        //if wall kicks false ->
                        if(curPiece.getType() == Piece.PieceType.STICK){
                            boolean noCollisions;
                            for(Point wallKick: Piece.I_COUNTERCLOCKWISE_WALL_KICKS[curPiece.getRotationIndex()]){
                                noCollisions = true;
                                Point wkPos = new Point(position.x + wallKick.x, position.y + wallKick.y);
                                for(Point p2: counterPiece.getBody()){
                                    Point p2Abs = getAbsolutePosition(p2, wkPos);
                                    if(checkCollisions(p2Abs, act)){
                                        noCollisions = false;
                                        break;
                                    }
                                }
                                if(noCollisions){
                                    curPiece = counterPiece;
                                    position.move(wkPos.x, wkPos.y);
                                    lastResult = Result.SUCCESS;
                                    clearRows();
                                    return Result.SUCCESS;
                                }
                            }
                        }else{
                            boolean noCollisions;
                            for(Point wallKick: Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[curPiece.getRotationIndex()]){
                                noCollisions = true;
                                Point wkPos = new Point(position.x + wallKick.x, position.y + wallKick.y);
                                for(Point p2: counterPiece.getBody()){
                                    Point p2Abs = getAbsolutePosition(p2, wkPos);
                                    if(checkCollisions(p2Abs, act)){
                                        noCollisions = false;
                                        break;
                                    }
                                }
                                if(noCollisions){
                                    curPiece = counterPiece;
                                    position.move(wkPos.x, wkPos.y);
                                    lastResult = Result.SUCCESS;
                                    clearRows();
                                    return Result.SUCCESS;
                                }
                            }
                        }
                        lastResult = Result.OUT_BOUNDS;
                        clearRows();
                        return Result.OUT_BOUNDS;
                    }
                }
                curPiece = counterPiece;
                lastResult = Result.SUCCESS;
                clearRows();
                return Result.SUCCESS;
            case DROP:
                position.move(position.x, dropHeight(curPiece, position.x));
                for (Point p1 : curPiece.getBody()) {
                    Point p1Abs = getAbsolutePosition(p1, position);
                    board[p1Abs.x].set(p1Abs.y, curPiece);
                }
                lastResult = Result.PLACE;
                clearRows();
                return Result.PLACE;
        }
        lastResult = Result.SUCCESS;
        clearRows();
        return Result.SUCCESS;
    }

    @Override
    public Board testMove(Action act) {
        try {
            Board temp = (Board) this.clone();
            temp.move(act);
            return temp;
        } catch (Exception e) {
            System.err.println("Invalid Board");
            return null;
        }
    }

    @Override
    public Piece getCurrentPiece() {
        return curPiece;
    }

    @Override
    public Point getCurrentPiecePosition() {
        return position;
    }

    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        for (Point point : p.getBody()) {
            Point temp = getAbsolutePosition(point, spawnPosition);
            if (temp.x >= width || temp.x < 0 || temp.y >= height || temp.y < 0) {
                throw new IllegalArgumentException();
            }
        }
        curPiece = p;
        position = spawnPosition;
    }

    private Point getAbsolutePosition(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof TetrisBoard){
            TetrisBoard otherBoard = (TetrisBoard) other;
            for(int i = 0; i < board.length;i++){
                for(int j = 0;j < board[i].size();j++){
                    //handling null cases
                    if(board[i].get(j) == null && otherBoard.board[i].get(j) == null){
                        continue;
                    }
                    if(board[i].get(j) == null && otherBoard.board[i].get(j) != null){
                        return false;
                    }
                    if(board[i].get(j) != null && otherBoard.board[i].get(j) == null){
                        return false;
                    }
                    //check for equality
                    if(!board[i].get(j).equals(otherBoard.board[i].get(j))){
                        return false;
                    }
                }
            }
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public Result getLastResult() {
        return lastResult;
    }

    @Override
    public Action getLastAction() {
        return lastAction;
    }

    @Override
    public int getRowsCleared() {
        return rowsCleared;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getMaxHeight() {
        int max = 0;
        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j > -1; j--) {
                if (board[i].get(j) != null) {
                    max = Math.max(j + 1, max);
                    break;
                }
            }
        }
        return max;
    }

    @Override
    public int dropHeight(Piece piece, int x) {
        int max = Integer.MIN_VALUE;
        for (Point p : piece.getBody()) {
            int pAbsX = x + p.x;
            if(pAbsX < 0 || pAbsX >= width) throw new IllegalArgumentException("Piece out of bounds");
            // System.out.println("Max of " + max + " and " + (getColumnHeight(pAbsX) - piece.getSkirt()[p.x]) + " is: " + (getColumnHeight(pAbsX) - piece.getSkirt()[p.x]));
            max = Math.max(max, getColumnHeight(pAbsX) - piece.getSkirt()[p.x]);
        }
        //ensure piece, not bounding box, hits the ground
        // System.out.println("Returned: " + max);
        return max;
    }

    @Override
    public int getColumnHeight(int x) {
        for (int j = height - 1; j > -1; j--) {
            if (board[x].get(j) != null) {
                return j + 1;
            }
        }
        return 0;
    }

    @Override
    public int getRowWidth(int y) {
        int count = 0;
        for (int i = 0; i < width; i++) {
            if (board[i].get(y) != null) {
                count++;
            }
        }
        return count;
    }
    public int getColWidth(int x) {
        int count = 0;
        for (int i = 0; i < height; i++) {
            if (board[x].get(i) != null) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Piece.PieceType getGrid(int x, int y) {
        try {
            return board[x].get(y).getType();
        } catch (Exception e) {
            return null;
        }
    }

    public TetrisBoard clone(){
        TetrisBoard ret = new TetrisBoard(this.width, this.height);
        ret.width = this.width;
        ret.height = this.height;
        ret.curPiece = this.curPiece;
        ret.position = new Point(position.x, position.y);
        ret.rowsCleared = rowsCleared;
        ret.lastAction = this.lastAction;
        ret.lastResult = this.lastResult;
        // ret.board = this.board.clone();
        LinkedList<Piece>[] newBoard = new LinkedList[width];
        for(int i = 0;i < width; i++){
            newBoard[i] = new LinkedList<Piece>();
            for(int j = 0;j < height; j++){
                newBoard[i].add(this.board[i].get(j));
            }
        }
        ret.board = newBoard;
        return ret;
    }
    public int countBadLocations() {
        int badSpots = 0;
        for(int i=0;i<width;i++){
            boolean isEmpty = false;
            for(int j=0;j<height;j++){
                if(isEmpty && board[i].get(j)!=null){
                    isEmpty = false;
                    badSpots++;
                    continue;
                }
                if(board[i].get(j)==null){
                    isEmpty = true;
                }
            }
        }
        return badSpots;
    }
}