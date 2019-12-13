package node;

import java.util.concurrent.atomic.AtomicBoolean;

public class MC_CNDFS
{
    public AtomicBoolean red;
    public AtomicBoolean blue;
    public AtomicBoolean[] cyan;

    public MC_CNDFS(int n)
    {
        red = new AtomicBoolean(false);
        blue = new AtomicBoolean(false);
        cyan = new AtomicBoolean[n];
        for (int i = 0; i < n; i++)
        {
            cyan[i] = new AtomicBoolean(false);
        }
    }
}