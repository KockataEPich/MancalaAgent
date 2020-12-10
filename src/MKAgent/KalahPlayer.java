package MKAgent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.PrintStream;

public class KalahPlayer {

    private Side ourSide;
    private Kalah kalah;
    protected int holes;
    private int maxDepth = 5;

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


        return 3*ourSeeds - oppSeeds;
    }

    protected int bestNextMove() {
        int bestMove = 0;
        int bestMoveHeuristics = Integer.MIN_VALUE;
        boolean isItUs = false;

        for(int i = 1; i <= this.holes; i++)
        {
            Move move = new Move(this.ourSide, i);
            if (this.kalah.isLegalMove(move))
            {
                Board board = new Board(this.kalah.getBoard());
                Side playedTurn = Kalah.makeMove(board, move);
                if(playedTurn == ourSide) isItUs = true;
                else isItUs = false;
                int heuristics = minmax(maxDepth, board, isItUs);
                if (heuristics >= bestMoveHeuristics)
                {
                    bestMove = i;
                    bestMoveHeuristics = heuristics;
                }


            }
        }

        return bestMove;
    }

    protected int minmax(int maxDepth, Board board,boolean isItUs) {
        // if we reach the desired depth
        if (maxDepth == 0 || this.kalah.gameOver()) {
            return heuristics(board);
        }
        // is it us??
        if (isItUs) {
            int max = Integer.MIN_VALUE;
            for (int i = 1; i <= holes; i++) {

                Move move = new Move(ourSide, i);

                if (this.kalah.isLegalMove(board, move)) {
                    // remember current board before generating the tree
                    Board lastBoard = board;
                    try {
                        lastBoard = board.clone();
                    } catch (CloneNotSupportedException e) {
                        System.err.println(e.getMessage());
                    }

                    Side nextSide = this.kalah.makeMove(board, move);
                    if(nextSide == this.ourSide) isItUs = true;
                    else isItUs = false;

                    int valueSoFar = minmax(maxDepth - 1, board, isItUs);

                    // undo the propagation from the recursion
                    board = lastBoard;

                    max = Math.max(max, valueSoFar);
                }
            }
            return max;
        } // end is it us??
        else {
            int min = Integer.MAX_VALUE;
            for (int i = 1; i <= holes; i++) {

                Move move = new Move(ourSide.opposite(), i);

                if (this.kalah.isLegalMove(board, move)) {
                    // remember current board before generating the tree
                    Board lastBoard = board;
                    try {
                        lastBoard = board.clone();
                    } catch (CloneNotSupportedException e) {
                        System.err.println(e.getMessage());
                    }

                    Side nextSide = this.kalah.makeMove(board, move);
                    if(nextSide == this.ourSide) isItUs = true;
                    else isItUs = false;

                    int valueSoFar = minmax(maxDepth - 1, board, isItUs);

                    // undo the propagation from the recursion
                    board = lastBoard;

                    min = Math.min(min, valueSoFar);
                }
            }
            return min;
        }
    }

    protected void swap() {
        this.ourSide = this.ourSide.opposite();
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
                    this.ourSide = Side.SOUTH;
                    Main.sendMsg(Protocol.createMoveMsg(1));
                } else {
                    this.ourSide = Side.NORTH;
                    maySwap = true;
                }

                while (true) {
                    msg = Main.recvMsg();
                    msgType = Protocol.getMessageType(msg);
                    if (msgType == MsgType.END) {
                        return;
                    }

                    if (msgType != MsgType.STATE) {
                        throw new InvalidMessageException("Expected a state message but got something else.");
                    }

                    Protocol.MoveTurn moveTurn = Protocol.interpretStateMsg(msg, this.kalah.getBoard());
                    if (moveTurn.move == -1) {
                        this.swap();
                    }

                    if (moveTurn.again && !moveTurn.end) {
                        msg = null;
                        int nextMove = this.bestNextMove();
                        if (maySwap) {
                            Board moveBoard = new Board(this.kalah.getBoard());
                            Kalah.makeMove(moveBoard, new Move(this.ourSide, nextMove));
                            int moveHeuristics = this.heuristics(moveBoard);
                            int swapHeuristics = -this.heuristics(this.kalah.getBoard());
                            if (swapHeuristics > moveHeuristics) {
                                this.swap();
                                msg = Protocol.createSwapMsg();
                            }
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