package MKAgent;

import java.util.ArrayList;

public class Node
{
    private Board board;
    private Node parent;
    private int depth;
    private ArrayList<Node> children;
    private Side side;
    private int value;

    public Node(Board currentBoard)
    {
        board = currentBoard;
        children = new ArrayList<Node>();
        parent = null;
    }

    public void setValue(int givenValue)
    {
        value = givenValue;
    }//setValue

    public int getValue()
    {
        return value;
    }

    public Side getSide()
    {
        return side;
    }

    public void setSide(Side givenSide)
    {
        side = givenSide;
    }//setSide

    public int getDepth()
    {
        return depth;
    }

    public void setDepth(int givenDepth)
    {
        depth = givenDepth;
    }

    public Board getBoard()
    {
        return board;
    }

    public Node getParent()
    {
        return parent;
    }

    public ArrayList<Node> getChildren()
    {
        return children;
    }

    public void setBoard(Board givenBoard)
    {
        board = givenBoard;
    }

    public void setParent(Node givenParent)
    {
        parent = givenParent;
    }

    public void addChild(Node child)
    {
        child.parent = this;
        this.children.add(child);
    }//addChild

    public Node getChild(int index)
    {
        return this.children.get(index);
    }//getChild
}
