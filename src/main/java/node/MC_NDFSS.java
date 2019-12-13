package node;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MC_NDFSS
{
    public volatile AtomicBoolean red;
    public volatile AtomicInteger count;
    public AtomicReferenceArray<Color> color;
    public AtomicBoolean[] pink;

    public MC_NDFSS(int n)
    {
        red = new AtomicBoolean(false);
        count = new AtomicInteger(0);
        color = new AtomicReferenceArray<Color>(n);
        pink = new AtomicBoolean[n];
        for (int i = 0; i < n; i++)
        {
            pink[i] = new AtomicBoolean(false);
            color.set(i, Color.WHITE);
        }
    }
}