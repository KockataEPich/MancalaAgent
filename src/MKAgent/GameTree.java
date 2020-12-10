package MKAgent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

public class GameTree
{

    public static void generateTree(Node root, Side givenSide, int maxDepth, int holes, Kalah kalah, Side ourSideRemembered)
    {

        // If we have reached the max given depth we evaluate the current board and start going back
        if (root.getDepth() > maxDepth)
        {
            root.setValue(BoardEvaluator.evaluateBoard(root.getBoard(), ourSideRemembered));
            return;
        }//if

        // Checking if if we have reached the end of the game even though we haven't reached the max depth
        boolean end = true;
        for (int i = 1; i <= holes; i++)
        {
            if(root.getBoard().getSeeds(givenSide, i) != 0)
                end = false;
        }//for

        if(end)
        {
            // Evaluate the current board, as this is the end
            root.setValue(BoardEvaluator.evaluateBoard(root.getBoard(), ourSideRemembered));
            return;
        }//if


        // For every possible move at current possition
        for (int i = 1; i <= holes; i++)
        {
            Move move = new Move(root.getSide(), i);
            Board boardNew = new Board(root.getBoard());

                // Check if the move is legal
                if (kalah.isLegalMove(boardNew, move))
                {
                    // If it is, make it, create a new node which has the state
                    //of the board and do it again

                    Side nextSide = Kalah.makeMove(boardNew, move);

                    Node newNode = new Node(boardNew);
                    newNode.setDepth(root.getDepth() + 1);
                    newNode.setSide(nextSide);
                    root.addChild(newNode);

                    generateTree(newNode, nextSide, maxDepth, holes, kalah, ourSideRemembered);

                    if(ourSideRemembered == root.getSide())
                    {
                        if(root.getBestMove() == 0)
                        {
                            root.setBestMove(i);
                            root.setValue(newNode.getValue());
                        }
                        else if(root.getValue() < newNode.getValue())
                        {
                            root.setBestMove(i);
                            root.setValue(newNode.getValue());
                        }//else if

                    }//if

                    if(ourSideRemembered != root.getSide())
                    {
                        if(root.getBestMove() == 0)
                        {
                            root.setBestMove(i);
                            root.setValue(newNode.getValue());
                        }
                        else if(root.getValue() > newNode.getValue())
                        {
                            root.setBestMove(i);
                            root.setValue(newNode.getValue());
                        }
                    }

                }//if
        }//for
    }//generateTree


    // Write the board to a file for debbugging purposes, slows down the game A LOT
    public static void writeBoardsToAFileDFS(Node rootNode)
    {
        try
        {

            PrintStream myWriter = new PrintStream(new FileOutputStream("boardOutputs.txt", true));
            myWriter.println("=====================================================================");
            myWriter.println("Player: " + rootNode.getSide() + " decision at depth: " + rootNode.getDepth() + " With Best" +
                    " Move: " + rootNode.getBestMove());
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

    // writeBoard to a file for debugging purposes - slows down the processing A LOT
    public static void writeBoardsToAFileBFS(Node rootNode)
    {
        try
        {

            PrintStream myWriter = new PrintStream(new FileOutputStream("boardOutputs.txt", true));
            myWriter.println("=====================================================================");
            myWriter.println("Player: " + rootNode.getSide() + " decision at depth: " + rootNode.getDepth() + " With Best" +
                    " Move: " + rootNode.getBestMove() + " And Value: " + rootNode.getValue());
            myWriter.println(rootNode.getBoard().toString());

            for(int i = 0; i < rootNode.getChildren().size(); i++)
            {

                myWriter.println("Player: " + rootNode.getSide() + " Decision Boards :");
                myWriter.print(rootNode.getChild(i).getBoard().toString());
                myWriter.println("Value of Board: " + rootNode.getChild(i).getValue());
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


}
