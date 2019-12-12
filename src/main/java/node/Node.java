package node;

import java.util.Iterator;
import java.util.Random;

//Base class for nodes

public class Node implements Comparable<Node>, Iterable<Node> {

    Node[] array;
    int size;

    public Node() {
        array = new Node[32];
        size = 0;
    }

    public void add(Node node) {
        array[size] = node;
        size++;
        if (size == array.length)
        {
            Node[] newarray = new Node[size * 2];
            for (int i = 0; i < size; i++)
            {
                newarray[i] = array[i];
            }
            array = newarray;
        } 
    }

    public int getSize()
    {
        return size;
    }

    public Node[] getArray()
    {
        return array;
    }

    public void setup()
    {
        //implement this for colorint initialization.
    }

    public int compareTo(Node o) {
        return this.hashCode() - o.hashCode();
    }

    public Iterator<Node> post() {
        return iterator();
    }
    
    public Iterator<Node> iterator() {
        return new PostIterator();
    }

    private class PostIterator implements Iterator<Node>
    {
        private boolean[] barray;
        int remain, cur;
        Random r;

        public PostIterator()
        {
            barray = new boolean[size];
            for (int i = 0; i < size; i++)
                barray[i] = true;
            remain = size;
            cur = -1;
            r = new Random();
        }

        public boolean hasNext() {
            return remain > 0;
        }

        public Node next() {
            if (!hasNext())
                return null;
            cur++;
            int i = r.nextInt(remain);
            while (i > 0 || !barray[cur % size])
            {
                if (barray[cur % size])
                    i--;
                cur++;
            }
            cur = cur % size;
            barray[cur] = false;
            remain--;
            return array[cur];
        }

    }

}