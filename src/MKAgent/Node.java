package MKAgent;

import java.util.ArrayList;

public class Node {
    private Board board;
    private Node parent;
    private int depth;
    private ArrayList<Node> children;

    public Node(Board currentBoard) {
        board = currentBoard;
        children = new ArrayList<Node>();
        parent = null;
    }

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

    public Node getChild(Node parent, int index)
    {
        return parent.children.get(index);
    }//getChild
}
