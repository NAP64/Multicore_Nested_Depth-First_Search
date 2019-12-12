import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import node.*;

public class Test
{

    public static void main(String[] args)
    {
        LinkedList<Node> tree = genTree(10000, 80);
        Iterator<Node> it = tree.get(0).post();
        System.out.println("" + tree.get(0).getSize());
        int i = 0, total2 = 0;
        while (it.hasNext())
        {
            i++;
            total2 += it.next().getSize();
        }
        System.out.println("" + i + ", " + total2);
    }

    //head is at index 0
    public static LinkedList<Node> genTree(int count, int passPercent)
    {
        Node head = new Node();
        LinkedList<Node> ll = new LinkedList<Node>();
        ll.add(head);
        Random r = new Random();
        for (int i = 0; i < count; i++)
        {
            Node cur = head;
            while (cur.getSize() > 1 && r.nextInt(100) < passPercent)
                cur = cur.getArray()[r.nextInt(cur.getSize()) + 1]; //avoid predecessor
            Node n = new Node(true);
            n.add(cur); //connect backward
            ll.add(n);
            cur.add(n);
        }
        return ll;
    }
}