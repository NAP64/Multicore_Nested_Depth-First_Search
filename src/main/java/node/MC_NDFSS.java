package node;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MC_NDFSS
{
    public volatile AtomicBoolean red;
    public volatile AtomicInteger count;

    public MC_NDFSS(int n)
    {
        red = new AtomicBoolean(false);
        count = new AtomicInteger(0);
    }
}