package node;

import java.util.concurrent.atomic.AtomicReference;

public class RB1Store
{
    public AtomicReference<Color> c;

    public RB1Store()
    {
        c = new AtomicReference<Color>();
        c.set(Color.WHITE);
    }
}