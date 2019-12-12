package node;

public class RB1Node extends Node
{
    public Color c;

    public RB1Node()
    {
        super();
    }

    public RB1Node(boolean b)
    {
        super(b);
    }

    @Override
    public void setup()
    {
        c = Color.WHITE;
    }
}