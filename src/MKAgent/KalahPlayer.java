package MKAgent;
import java.io.*;

public class KalahPlayer {

    private Side ourSide;
    private Kalah kalah;
    protected int holes;
    private int givenTime = 130;

    private int alpha = Integer.MIN_VALUE;
    private int beta = Integer.MAX_VALUE;

    boolean weStart;

    int moveCount = 0;

    // make sure execution fits within specified time constraint
    // TODO: parametrize time constraint
    boolean executionTooLong = false;
    long executionStartTime;

    // prevent minmax from generating a skewed never ending play tree
    boolean isGameOver;

    public KalahPlayer(int holes, int seeds) {
        this.holes = holes;
        this.kalah = new Kalah(new Board(holes, seeds));
    }

    protected int heuristics(Board b) {
        int ourSeeds = 2*b.getSeedsInStore(this.ourSide);

        int oppSeeds = 2*b.getSeedsInStore(this.ourSide.opposite());

        for (int i = 1; i <= this.holes; ++i) {
            ourSeeds += b.getSeeds(this.ourSide, i);
            oppSeeds += b.getSeeds(this.ourSide.opposite(), i);
        }


        return ourSeeds - oppSeeds;
    }

//******************************    best next move with iterative deepening   *****************************************
    protected int bestNextMove()
    {
        // alpha maximizes max rounds
        // beta minimizes min rounds
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int bestMove = 0;
        int bestHeuristics = Integer.MIN_VALUE;

        // on each play keeps track of the maximizing player (us)
        boolean isItUs = false;
        // recursive branching reached the end state
        isGameOver = true;
        // track the time for our iterative deepening search
        executionTooLong = false;
        executionStartTime = System.currentTimeMillis();
        // begin iterative deepening
        while(!executionTooLong){
            for(int depth=0; ; depth++)
            {
                isGameOver = true;
                alpha = Integer.MIN_VALUE;
                beta = Integer.MAX_VALUE;
                //Go through all the possible moves
                for (int i = 1; i <= holes; i++) {
                    Move move = new Move(this.ourSide, i);
                    if (kalah.isLegalMove(move)) {
                        Board board = new Board(this.kalah.getBoard());
                        Side playedTurn = Kalah.makeMove(board, move);
                        if(playedTurn == this.ourSide) isItUs = true;
                        else isItUs = false;
                        // solves a play starting bug
                        if(weStart && moveCount == 1)
                            isItUs = false;

                        int [] minmaxReturnValues = minmax(depth, board, isItUs, alpha, beta);
                        int heuristics = minmaxReturnValues[0];

                        // we are always maximizing so only alpha matters
                        alpha = minmaxReturnValues[1];
                        alpha = Math.max(alpha, heuristics);

                        if (heuristics > bestHeuristics) {
                            bestHeuristics = heuristics;
                            bestMove = i;
                        }
                    }
                    if(System.currentTimeMillis() - executionStartTime > givenTime){
                        System.err.println("Depth reached : " + depth);
                        executionTooLong = true;
                        break;
                    }
                }
                if(isGameOver) {
                    executionTooLong = true;
                    break;
                }
                // a normal move takes now too much to inspect at this depth, no point in taking it further
                if(executionTooLong){
                    break;
                }
            }// for
        }// while
        return bestMove;
    }

////***************************************************  MINMAX  ********************************************************
    protected int[] minmax(int maxDepth, Board board, boolean isItUs, int alpha, int beta) {
        // when recursion call stack is finished
        if (maxDepth == 0 || this.kalah.gameOver(board)) {
            if(maxDepth >= 0 && !this.kalah.gameOver(board))
                isGameOver = false;
            int heuristicValue = heuristics(board);
            return new int[]{heuristicValue, alpha, beta};
        }
        // is it us?? == MAX
        if (isItUs) {
            int valueMax = Integer.MIN_VALUE;
            for (int i = 1; i <= holes; i++) {
                Move move = new Move(this.ourSide, i);

                if (this.kalah.isLegalMove(board, move)) {
                    // remember current board before generating the tree
                    Board lastBoard = board;
                    try {
                        lastBoard = board.clone();
                    } catch (CloneNotSupportedException e) {
                        System.err.println(e.getMessage());
                    }

                    Side nextPlay = this.kalah.makeMove(board, move);
                    if(nextPlay == this.ourSide) isItUs = true;
                    else isItUs = false;

                    int maxValueSoFar = minmax(maxDepth - 1, board, isItUs, alpha, beta)[0];

                    // undo the propagation from the recursion
                    board = lastBoard;
                    valueMax = Math.max(valueMax, maxValueSoFar);
                    // prune unnecessary values that won't be selected anyway
                    alpha = Math.max(alpha, maxValueSoFar);
                    if(beta <= alpha) break;
                }
//                if((System.currentTimeMillis() - executionStartTime) > givenTime){
//                    executionTooLong = true;
//                    break;
//                }
            }
            return new int[]{valueMax, alpha, beta};
        } // end is it us??
        // not us == MIN
        else {
            int valueMin = Integer.MAX_VALUE;
            for (int i = 1; i <= holes; i++) {
                Move move = new Move(this.ourSide.opposite(), i);

                if (this.kalah.isLegalMove(board, move)) {
                    // remember current board before generating the tree
                    Board lastBoard = board;
                    try {
                        lastBoard = board.clone();
                    } catch (CloneNotSupportedException e) {
                        System.err.println(e.getMessage());
                    }

                    Side nextPlay = this.kalah.makeMove(board, move);
                    if(nextPlay == this.ourSide) isItUs = true;
                    else isItUs = false;

                    int minValueSoFar = minmax(maxDepth - 1, board, isItUs, alpha, beta)[0];

                    // undo the propagation from the recursion
                    board = lastBoard;
                    valueMin = Math.min(valueMin, minValueSoFar);
                    // prune unnecessary values that won't be selected anyway
                    beta = Math.min( beta, minValueSoFar);
                    if(beta <= alpha) break;
                }
//                if((System.currentTimeMillis() - executionStartTime) > givenTime){
//                    executionTooLong = true;
//                    break;
//                }
            }
            return new int[] {valueMin, alpha, beta};
        }
    }

    protected void swap() {
        this.ourSide = this.ourSide.opposite();
    }

    protected int calculateSwapMove(){
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int bestMove = 0;
        int bestHeuristicsSwap = Integer.MIN_VALUE;
        int bestHeuristicsNoSwap = Integer.MIN_VALUE;

        boolean isItUs = false;

        isGameOver = true;

        executionTooLong = false;
        executionStartTime = System.currentTimeMillis();

        // NO SWAP
        // Calculate all the possible moves within given time
        while(!executionTooLong){
            for(int depth=0; ; depth++)
            {
                isGameOver = true;
                alpha = Integer.MIN_VALUE;
                beta = Integer.MAX_VALUE;
                //Go through all the possible moves
                for (int i = 1; i <= holes; i++) {
                    Move move = new Move(this.ourSide, i);

                    if (kalah.isLegalMove(move)) {
                        Board board = new Board(this.kalah.getBoard());
                        Side playedTurn = Kalah.makeMove(board, move);
                        if(playedTurn == this.ourSide) isItUs = true;
                        else isItUs = false;

                        int [] minmaxReturnValues = minmax(depth, board, isItUs, alpha, beta);
                        int heuristics = minmaxReturnValues[0];

                        // we are always maximizing so only alpha matters
                        alpha = minmaxReturnValues[1];
                        alpha = Math.max(alpha, heuristics);

                        if (heuristics > bestHeuristicsNoSwap) {
                            bestHeuristicsNoSwap = heuristics;
                            bestMove = i;
                        }
                    }
                    if(System.currentTimeMillis() - executionStartTime > givenTime / 2){
                        System.err.println("Depth reached : " + depth);
                        executionTooLong = true;
                        break;
                    }
                }
                if(isGameOver) {
                    executionTooLong = true;
                    break;
                }
                // a normal move takes now too much to inspect at this depth, no point in taking it further
                if(executionTooLong){
                    break;
                }
            }// for
        }// while
        swap();
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
        executionTooLong = false;
        executionStartTime = System.currentTimeMillis();

        // SWAP
        // Calculate all the possible moves within given time
        while(!executionTooLong){
            for(int depth=0; ; depth++)
            {
                isGameOver = true;
                alpha = Integer.MIN_VALUE;
                beta = Integer.MAX_VALUE;
                //Go through all the possible moves
                for (int i = 1; i <= holes; i++) {
                    Move move = new Move(this.ourSide, i);

                    if (kalah.isLegalMove(move)) {
                        Board board = new Board(this.kalah.getBoard());
                        Side playedTurn = Kalah.makeMove(board, move);
                        if(playedTurn == this.ourSide) isItUs = true;
                        else isItUs = false;

                        int [] minmaxReturnValues = minmax(depth, board, isItUs, alpha, beta);
                        int heuristics = minmaxReturnValues[0];

                        // we are always maximizing so only alpha matters
                        beta = minmaxReturnValues[2];
                        beta = Math.min(beta, heuristics);

                        if (heuristics > bestHeuristicsSwap) {
                            bestHeuristicsSwap = heuristics;
                            bestMove = i;
                        }
                    }
                    if(System.currentTimeMillis() - executionStartTime > givenTime / 2){
                        System.err.println("Depth reached : " + depth);
                        executionTooLong = true;
                        break;
                    }
                }
                if(isGameOver) {
                    executionTooLong = true;
                    break;
                }
                // a normal move takes now too much to inspect at this depth, no point in taking it further
                if(executionTooLong){
                    break;
                }
            }// for
        }// while
        swap();

        if(bestHeuristicsSwap > bestHeuristicsNoSwap) return 1;
        else return bestMove;
    }

    public void play() throws IOException, InvalidMessageException {
        boolean maySwap = false;
        String msg = Main.recvMsg();
        MsgType msgType = Protocol.getMessageType(msg);
        if (msgType != MsgType.END) {
            if (msgType != MsgType.START) {
                throw new InvalidMessageException("Expected a start message but got something else.");
            } else {
                if (Protocol.interpretStartMsg(msg)) {
                    weStart = true;
                    moveCount++;
                    this.ourSide = Side.SOUTH;
                    Main.sendMsg(Protocol.createMoveMsg(1));
                } else {
                    weStart = false;
                    this.ourSide = Side.NORTH;
                    maySwap = true;
                }
                // game started, sides were alloted
                // from this point on, messages can only be of type STATE and END
                while (true) {
                    msg = Main.recvMsg();
                    msgType = Protocol.getMessageType(msg);
                    if (msgType == MsgType.END) {
                        return;
                    }

                    if (msgType != MsgType.STATE) {
                        throw new InvalidMessageException("Expected a state message but got something else.");
                    }

                    // opponent swapped sides
                    Protocol.MoveTurn moveTurn = Protocol.interpretStateMsg(msg, this.kalah.getBoard());
                    if (moveTurn.move == -1) {
                        this.swap();
                    }

                    // our turn now, game not ended
                    if (moveTurn.again && !moveTurn.end) {
//                        msg = null;
//                        int nextMove = this.bestNextMove();
//                        if (maySwap) {
//                            Board moveBoard = new Board(this.kalah.getBoard());
//                            Kalah.makeMove(moveBoard, new Move(this.ourSide, nextMove));
//                            int moveHeuristics = this.heuristics(moveBoard);
//                            int swapHeuristics = -this.heuristics(this.kalah.getBoard());
//                            if (swapHeuristics > moveHeuristics) {
//                                this.swap();
//                                msg = Protocol.createSwapMsg();
//                            }
//                        }
                        msg = null;

                        // we dont know yet which move is the best
                        int nextMove = 0;

                        //Check whether to swap or not
                        if (maySwap) {
                            moveCount++;
                            int nextMoveAfterSwap = calculateSwapMove();

                            // if swapping is better do it, and get the best swapped move
                            if (nextMoveAfterSwap == 1) {
                                System.err.println("Swapping");
                                swap();
                                msg = Protocol.createSwapMsg();
                            }

                            // otherwise it will simply not swap and return the best move
                            else
                                nextMove = nextMoveAfterSwap;
                        }
                        else
                        {
                            moveCount++;
                            nextMove = this.bestNextMove();
                        }

                        maySwap = false;
                        if (msg == null) {
                            msg = Protocol.createMoveMsg(nextMove);
                        }

                        Main.sendMsg(msg);
                    }
                }
            }
        }
    }



}