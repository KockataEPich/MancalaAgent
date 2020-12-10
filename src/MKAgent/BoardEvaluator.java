package MKAgent;

// Class that is concerned with Evaluating the board.
public class BoardEvaluator
{
    private static int mankalahModifier = 5;
    private static int seedsModifier = 1;


    // Returns an int which is or heuristic
    public static int evaluateBoard(Board board, Side ourSide)
    {
        int finalScoreOur = 0;
        int finalScoreEnemy = 0;

        for(int i = 1; i < board.getNoOfHoles(); i++)
        {
            finalScoreOur += board.getSeeds(ourSide, i)*seedsModifier;
        }//for

        finalScoreOur += board.getSeedsInStore(ourSide)*mankalahModifier;

        for(int i = 1; i < board.getNoOfHoles(); i++)
        {
            finalScoreEnemy += board.getSeeds(ourSide.opposite(), i)*seedsModifier;
        }//for

        finalScoreEnemy += board.getSeedsInStore(ourSide.opposite())*mankalahModifier;

        return finalScoreOur - finalScoreEnemy;
    }//evaluateBoaard
}
