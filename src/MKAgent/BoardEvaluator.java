package MKAgent;

// Class that is concerned with Evaluating the board.
public class BoardEvaluator {

    public static int mancalaWeight = 2;
    public static int holesWeight = 1;
    public static int kalahMoveWeight = 5;
    public static int ourPercentageWeight = 1;
    public static int enemyPercentageWeight = 1;

    // Returns an int which is or heuristic
    public static int evaluateBoard(Board board, Side ourSide) {
        return    (mancalaEvaluate(board,ourSide)              * mancalaWeight)
                + (holesEvaluate(board, ourSide)               * holesWeight)
                + (kalahMove(board, ourSide)                   * kalahMoveWeight)
                + (ourMancalaPercentage(board, ourSide)        * ourPercentageWeight)
                + (enemyMancalaPercentage(board, ourSide)      * enemyPercentageWeight);
    } // evaluateBoard

    public static int mancalaEvaluate(Board board, Side ourSide) {
        return board.getSeedsInStore(ourSide) - board.getSeedsInStore(ourSide.opposite());
    }

    public static int holesEvaluate(Board board, Side ourSide) {
        int finalScoreOur = 0;
        int finalScoreEnemy = 0;

        for(int i = 1; i <= board.getNoOfHoles(); i++) {
            finalScoreOur += board.getSeeds(ourSide, i);
            finalScoreEnemy += board.getSeeds(ourSide.opposite(), i);
        }
        return finalScoreOur - finalScoreEnemy;
    }

    public static int ourMancalaPercentage(Board board, Side ourSide) {
        return board.getSeedsInStore(ourSide) * 98 / 100;
    }

    public static int enemyMancalaPercentage(Board board, Side ourSide) {
        return -board.getSeedsInStore(ourSide.opposite()) * 98 / 100;
    }

    // Check whether repeat move is possible
    // 0-7
    public static int kalahMove(Board board, Side ourSide) {
        int extraMoves = 0;

        for (int i = 1; i <= board.getNoOfHoles(); i++) {
            if (i + board.getSeeds(ourSide, i) == 8 || i + board.getSeeds(ourSide, i) == 23) {
                extraMoves++;
            }
        }
        return extraMoves;
    }
}
