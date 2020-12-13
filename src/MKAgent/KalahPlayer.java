package MKAgent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.PrintStream;

public class KalahPlayer {

    private Side ourSide;
    private Kalah kalah;
    private int holes;
    private int maxDepth = 5;

    public KalahPlayer(int holes, int seeds)
    {
        this.ourSide = Side.SOUTH;
        this.holes = holes;
        this.kalah = new Kalah(new Board(holes, seeds));
    }

    protected int heuristics(Board b) {
        int ourSeeds = 2 * b.getSeedsInStore(this.ourSide);

        int oppSeeds = 2 * b.getSeedsInStore(this.ourSide.opposite());

        for (int i = 1; i <= this.holes; ++i) {
            ourSeeds += b.getSeeds(this.ourSide, i);
            oppSeeds += b.getSeeds(this.ourSide.opposite(), i);
        }


        return 3*ourSeeds - oppSeeds;
    }

    public int bestNextMove(Side givenSide, int maxDepth, int currentDepth, Board board,  int givenAlpha, int givenBeta)
    {
        currentDepth ++;
        // If we have reached the max given depth we evaluate the current board and start going back
        if (currentDepth == maxDepth || Kalah.gameOver(board))
            return BoardEvaluator.evaluateBoard(board, this.ourSide);

        int currentBestMove = 0;
        int ourValue = 0;
        // For every possible move at current possition
        for (int i = 1; i <= this.holes; i++)
        {
            Move move = new Move(givenSide, i);
            Board boardNew = new Board(board);

            // Check if the move is legal
            if (kalah.isLegalMove(boardNew, move))
            {
                // If it is do it again

                int branchValue = bestNextMove(Kalah.makeMove(boardNew, move), maxDepth, currentDepth, boardNew,
                                                                                                givenAlpha, givenBeta);

                if (currentBestMove == 0)
                {
                    currentBestMove = i;
                    ourValue = branchValue;
                }//if
                else if((this.ourSide == givenSide && ourValue < branchValue) ||
                                                                (this.ourSide != givenSide && ourValue > branchValue))
                {
                    currentBestMove = i;
                    ourValue = branchValue;
                }//if

                if (this.ourSide == givenSide && ourValue > givenAlpha)
                    givenAlpha = ourValue;

                if (this.ourSide != givenSide && ourValue < givenBeta)
                    givenBeta = ourValue;

                if(givenAlpha > givenBeta)
                    return ourValue;
            }//if
        }//for

        if(currentDepth == 0)
            return currentBestMove;
        else
            return ourValue;
    }//bestNextMove

    protected void swap() {
        this.ourSide = this.ourSide.opposite();
    }

    public void play() throws IOException, InvalidMessageException {
        boolean maySwap = false;
        String msg = Main.recvMsg();
        MsgType msgType = Protocol.getMessageType(msg);
        if (msgType != MsgType.END) {
            if (msgType != MsgType.START)
            {
                throw new InvalidMessageException("Expected a start message but got something else.");
            } else {
                if (Protocol.interpretStartMsg(msg)) {
                    this.ourSide = Side.SOUTH;
                    Main.sendMsg(Protocol.createMoveMsg(1));
                } else
                    {
                    this.ourSide = Side.NORTH;
                    maySwap = true;
                }

                while (true)
                {
                    msg = Main.recvMsg();
                    msgType = Protocol.getMessageType(msg);
                    if (msgType == MsgType.END)
                    {
                        return;
                    }

                    if (msgType != MsgType.STATE)
                    {
                        throw new InvalidMessageException("Expected a state message but got something else.");
                    }

                    Protocol.MoveTurn moveTurn = Protocol.interpretStateMsg(msg, this.kalah.getBoard());
                    if (moveTurn.move == -1)
                    {
                        this.swap();
                    }

                    if (moveTurn.again && !moveTurn.end)
                    {
                        msg = null;

                        int nextMove = this.bestNextMove(this.ourSide, maxDepth, -1, this.kalah.getBoard(),
                                                                                  Integer.MIN_VALUE, Integer.MAX_VALUE);

                        if (maySwap)
                        {
                            Board moveBoard = new Board(7,7);
                            int anotherMove = this.bestNextMove(this.ourSide, maxDepth, -1,
                                    moveBoard, Integer.MIN_VALUE, Integer.MAX_VALUE);

                            Kalah.makeMove(moveBoard, new Move(this.ourSide, anotherMove));

                            if (moveBoard.equals(this.kalah.getBoard()))
                            {
                                this.swap();
                                msg = Protocol.createSwapMsg();
                            }
                        }

                        maySwap = false;
                        if (msg == null)
                        {
                            msg = Protocol.createMoveMsg(nextMove);
                        }

                        Main.sendMsg(msg);
                    }
                }
            }
        }
    }



}