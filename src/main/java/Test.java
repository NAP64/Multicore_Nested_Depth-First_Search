import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import node.*;

public class Test {
    //Java doesn't support alias or macro...
    //workaround to minimize change in following code
    static class CurNode extends RB1Node {
        public CurNode()
        {
            super();
        }

        public CurNode(boolean b)
        {
            super(b);
        }
    }

    public static void main(String[] args)
    {
        LinkedList<CurNode> tree = genTree(10000, 80);
        Iterator<Node> it = tree.get(0).post();
        System.out.println("" + tree.get(0).getSize());
        int i = 0, total2 = 0;
        while (it.hasNext()) {
            i++;
            total2 += it.next().getSize();
        }
        System.out.println("" + i + ", " + total2);

        for (CurNode j : tree) {
            j.setup();
        }

        System.out.println("" + RB1dfs(tree.get(0)));
    }

    // head is at index 0
    public static LinkedList<CurNode> genTree(int count, int passPercent)
    {
        CurNode head = new CurNode(true);
        LinkedList<CurNode> ll = new LinkedList<CurNode>();
        ll.add(head);
        Random r = new Random();
        for (int i = 0; i < count; i++)
        {
            CurNode cur = head;
            while (cur.getSize() > 1 && r.nextInt(100) < passPercent)
                cur = (CurNode)cur.getArray()[r.nextInt(cur.getSize())];
            CurNode n = new CurNode(true);
            ll.add(n);
            cur.add(n);
        }
        return ll;
    }

    //true when cycle detected
    public static boolean RB1dfs(CurNode head)
    {
        return RB1dfsBlue(head);
    }

    public static boolean RB1dfsBlue(CurNode head)
    {
        head.c = Color.CYAN;
        Iterator<Node> it = head.post();
        while (it.hasNext())
        {
            CurNode cur = (CurNode)it.next();
            if (cur.c == Color.WHITE)
            {
                if (RB1dfsBlue(cur))
                    return true;
            }
        }
        if (head.isAccepting())
        {
            if (RB1dfsRed(head))
                return true;
            head.c = Color.RED;
        }
        else
        {
            head.c = Color.BLUE;
        }
        return false;
    }

    public static boolean RB1dfsRed(CurNode head)
    {
        Iterator<Node> it = head.post();
        while (it.hasNext())
        {
            CurNode cur = (CurNode)it.next();
            if (cur.c == Color.CYAN)
                return true;
            else if (cur.c == Color.BLUE)
            {
                cur.c = Color.RED;
                return RB1dfsRed(cur);
            }
        }
        return false;
    }
}