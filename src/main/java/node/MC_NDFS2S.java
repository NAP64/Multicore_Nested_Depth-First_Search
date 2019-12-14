package node;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MC_NDFS2S
{
    public AtomicBoolean red;
    public AtomicInteger count;

    public MC_NDFS2S(int n)
    {
        red = new AtomicBoolean(false);
        count = new AtomicInteger(0);
    }
}