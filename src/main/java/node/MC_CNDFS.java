package node;

import java.util.concurrent.atomic.AtomicBoolean;

public class MC_CNDFS
{
    public AtomicBoolean red;
    public AtomicBoolean blue;

    public MC_CNDFS(int n)
    {
        red = new AtomicBoolean(false);
        blue = new AtomicBoolean(false);
    }
}