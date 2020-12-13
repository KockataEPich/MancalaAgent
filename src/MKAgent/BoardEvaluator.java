package MKAgent;

// Class that is concerned with Evaluating the board.
public class BoardEvaluator
{

     static int relationWeight = 1;
     static int percentageWeight = 0;


    // Returns an int which is or heuristic
    public static int evaluateBoard(Board board, Side ourSide)
    {
        return   (evaluateBoardRelation(board,ourSide) * relationWeight)
               + (evaluateMancalaPercentage(board, ourSide) * percentageWeight);

    }//evaluateBoard


    public static int evaluateBoardRelation(Board board, Side ourSide)
    {
        int mankalahModifier = 2;
        int seedsModifier = 1;

        int finalScoreOur = 0;
        int finalScoreEnemy = 0;
        for(int i = 1; i <= board.getNoOfHoles(); i++)
        {
            finalScoreOur += board.getSeeds(ourSide, i);
            finalScoreEnemy += board.getSeeds(ourSide.opposite(), i);
        }//for

        finalScoreOur *= seedsModifier ;
        finalScoreEnemy *= seedsModifier ;

        finalScoreOur += board.getSeedsInStore(ourSide)*mankalahModifier;
        finalScoreEnemy += board.getSeedsInStore(ourSide.opposite())*mankalahModifier;


        return finalScoreOur - finalScoreEnemy;
    }

    public static int evaluateMancalaPercentage(Board board, Side ourSide)
    {
        int ourPercentage = board.getSeedsInStore(ourSide) * 98 / 100;
        int enemyPercentage = board.getSeedsInStore(ourSide.opposite()) * 98 / 100;

        if(ourPercentage > enemyPercentage)
            return ourPercentage;
        else
            return 0 - enemyPercentage;
    }//evaluateBoardPercentage


}
