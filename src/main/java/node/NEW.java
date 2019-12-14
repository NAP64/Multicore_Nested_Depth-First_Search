package node;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NEW
{
    public AtomicBoolean red;
    public AtomicInteger count;
    public AtomicBoolean blue;

    public NEW(int n)
    {
        red = new AtomicBoolean(false);
        blue = new AtomicBoolean(false);
        count = new AtomicInteger(0);
    }
}