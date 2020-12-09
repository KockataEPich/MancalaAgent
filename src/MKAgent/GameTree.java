package MKAgent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class GameTree
{

    public static void generateTree(Node root, Side givenSide, int maxDepth, int holes, Kalah kalah)
    {

        if (root.getDepth() > maxDepth)
            return;

        for (int i = 1; i <= holes; i++)
        {
            Move move = new Move(root.getSide(), i);
            Board boardNew = new Board(root.getBoard());

                if (kalah.isLegalMove(boardNew, move))
                {


                    Side nextSide = Kalah.makeMove(boardNew, move);

                    Node newNode = new Node(boardNew);
                    newNode.setDepth(root.getDepth() + 1);
                    newNode.setSide(nextSide);
                    root.addChild(newNode);

                    generateTree(newNode, nextSide, maxDepth, holes, kalah);
                }//if
        }//for



    }//generateTree


    public static void writeBoardsToAFileDFS(Node rootNode)
    {
        try
        {

            PrintStream myWriter = new PrintStream(new FileOutputStream("boardOutputs.txt", true));
            myWriter.println("=====================================================================");
            myWriter.println("Player: " + rootNode.getSide() + " decision at depth: " + rootNode.getDepth());
            myWriter.println(rootNode.getBoard().toString());

            for(int i = 0; i < rootNode.getChildren().size(); i++)
            {
                myWriter.println("Player: " + rootNode.getSide() + " Decides to play :");
                myWriter.println(rootNode.getChild(i).getBoard().toString());
                myWriter.println();
                writeBoardsToAFileDFS(rootNode.getChild(i));
                myWriter.println();
                myWriter.println();
            }//for


            myWriter.close();
        }//try
        catch(IOException e){}

    }

    public static void writeBoardsToAFileBFS(Node rootNode)
    {
        try
        {

            PrintStream myWriter = new PrintStream(new FileOutputStream("boardOutputs.txt", true));
            myWriter.println("=====================================================================");
            myWriter.println("Player: " + rootNode.getSide() + " decision at depth: " + rootNode.getDepth());
            myWriter.println(rootNode.getBoard().toString());

            for(int i = 0; i < rootNode.getChildren().size(); i++)
            {

                myWriter.println("Player: " + rootNode.getSide() + " Decision Boards :");
                myWriter.println(rootNode.getChild(i).getBoard().toString());
                myWriter.println();

            }//for

            for(int i = 0; i < rootNode.getChildren().size(); i++)
            {
                writeBoardsToAFileBFS(rootNode.getChild(i));
            }

            myWriter.close();
        }//try
        catch(IOException e){}

    }


    public int EvaluateNodes(Node A, Node B)
    {
        if(A.getValue() == B.getValue())
            return 0;
        else if(A.getValue() > B.getValue())
        {
            return 1;
        }//
        else
            return -1;
    }//EvaluateNodes
}
