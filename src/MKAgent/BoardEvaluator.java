package MKAgent;

// Class that is concerned with Evaluating the board.
public class BoardEvaluator
{

    public static int ourMancalaWeight = 5;
    public static int enemyMancalaWeight = -1;
    public static int ourHolesWeight = 0;
    public static int enemyHolesWeight = 0;
    public static int ourPercentageWeight = 0;
    public static int enemyPercentageWeight = 0;


    // Returns an int which is or heuristic
    public static int evaluateBoard(Board board, Side ourSide)
    {
        return    (ourMancalaEvaluate(board,ourSide)           * ourMancalaWeight)
                + (enemyMancalaEvaluate(board, ourSide)        * enemyMancalaWeight)
                + (ourHolesEvaluate(board, ourSide)            * ourHolesWeight)
                + (enemyHolesEvaluate(board, ourSide)          * enemyHolesWeight)
                + (ourMancalaPercentage(board, ourSide)        * ourPercentageWeight)
                + (enemyMancalaPercentage(board, ourSide)      * enemyPercentageWeight);


    }//evaluateBoard

    public static int ourMancalaEvaluate(Board board, Side ourSide)
    {
        return board.getSeedsInStore(ourSide);
    }//myMancalaEvaluate

    public static int enemyMancalaEvaluate(Board board, Side ourSide)
    {
        return -board.getSeedsInStore(ourSide.opposite());
    }

    public static int ourHolesEvaluate(Board board, Side ourSide)
    {
        int finalScoreOur = 0;
        for(int i = 1; i <= board.getNoOfHoles(); i++)
        {
            finalScoreOur += board.getSeeds(ourSide, i);
        }//for

        return finalScoreOur;
    }

    public static int enemyHolesEvaluate(Board board, Side ourSide)
    {
        int finalScoreEnemy = 0;
        for(int i = 1; i <= board.getNoOfHoles(); i++)
        {
            finalScoreEnemy += board.getSeeds(ourSide.opposite(), i);
        }//for

        return -finalScoreEnemy;
    }

    public static int ourMancalaPercentage(Board board, Side ourSide)
    {
        return board.getSeedsInStore(ourSide) * 98 / 100;
    }//evaluateBoardPercentage

    public static int enemyMancalaPercentage(Board board, Side ourSide)
    {
        return -board.getSeedsInStore(ourSide.opposite()) * 98 / 100;
    }

}
