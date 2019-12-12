import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import node.*;
import ndfs.*;

public class Test {
	private static final int WARMUPS = 2;

    public static void main(String[] args) throws InterruptedException
    {
        //int graph_size = Integer.parseInt(args[0]);
		//int thread_count = Integer.parseInt(args[1]);
        //int iterations = Integer.parseInt(args[2]);
        int graph_size = 60000;
        int thread_count = 2;
        int iterations = 3;
        int extra_edge = 20;


        LinkedList<Node> graph = genTree(graph_size, 80);

        for (int i=0; i<WARMUPS; i++) {
			run(graph, thread_count);
		}
		
		for (int j=0; j<iterations; j++) {
			run(graph, thread_count);
        }

        for (int i=0; i<WARMUPS; i++) {
			run1(graph, thread_count);
		}
		
		for (int j=0; j<iterations; j++) {
			run1(graph, thread_count);
        }

        for (int i=0; i<WARMUPS; i++) {
			run2(graph, thread_count);
		}
		
		for (int j=0; j<iterations; j++) {
			run2(graph, thread_count);
        }
        
    }

    private static void run(LinkedList<Node> graph, int thread_count) throws InterruptedException
    {
        MC_NDFS_Thread[] threads = new MC_NDFS_Thread[thread_count];
        MC_NDFS_Thread.reset();
        MC_NDFS_Thread.setup_graph(graph, thread_count);

        for (int i=0; i<thread_count; i++) {
			threads[i] = new MC_NDFS_Thread(graph.get(0));
		}
		
		for (int i=0; i<thread_count; i++) {
			threads[i].start();
        }
        
		boolean hasCycle = false;
		long startTime = System.currentTimeMillis();
		for (int i=0; i<thread_count; i++) {
			threads[i].join();
			hasCycle = hasCycle || threads[i].hasCycle();
		}
		long endTime = System.currentTimeMillis();
		
		if (hasCycle) 
			System.out.println("Cycle detected. Runtime = "+(endTime-startTime)+" ms");
		else
			System.out.println("No cycles detected. Runtime = "+(endTime-startTime)+" ms");

    }

    private static void run1(LinkedList<Node> graph, int thread_count) throws InterruptedException
    {
        MC_NDFS2_Thread[] threads = new MC_NDFS2_Thread[thread_count];
        MC_NDFS2_Thread.reset();
        MC_NDFS2_Thread.setup_graph(graph, thread_count);

        for (int i=0; i<thread_count; i++) {
			threads[i] = new MC_NDFS2_Thread(graph.get(0));
		}
		
		for (int i=0; i<thread_count; i++) {
			threads[i].start();
        }
        
		boolean hasCycle = false;
		long startTime = System.currentTimeMillis();
		for (int i=0; i<thread_count; i++) {
			threads[i].join();
			hasCycle = hasCycle || threads[i].hasCycle();
		}
		long endTime = System.currentTimeMillis();
		
		if (hasCycle) 
			System.out.println("Cycle detected. Runtime = "+(endTime-startTime)+" ms");
		else
			System.out.println("No cycles detected. Runtime = "+(endTime-startTime)+" ms");

    }

    private static void run2(LinkedList<Node> graph, int thread_count) throws InterruptedException
    {
        RB1_Thread[] threads = new RB1_Thread[thread_count];
        RB1_Thread.reset();
        RB1_Thread.setup_graph(graph);

        for (int i=0; i<thread_count; i++) {
			threads[i] = new RB1_Thread(graph.get(0));
		}
		
		for (int i=0; i<thread_count; i++) {
			threads[i].start();
        }
        
		boolean hasCycle = false;
		long startTime = System.currentTimeMillis();
		for (int i=0; i<thread_count; i++) {
			threads[i].join();
			hasCycle = hasCycle || threads[i].hasCycle();
		}
		long endTime = System.currentTimeMillis();
		
		if (hasCycle) 
			System.out.println("Cycle detected. Runtime = "+(endTime-startTime)+" ms");
		else
			System.out.println("No cycles detected. Runtime = "+(endTime-startTime)+" ms");

    }

    // head is at index 0
    public static LinkedList<Node> genTree(int count, int passPercent)
    {
        Node head = new Node(true);
        LinkedList<Node> ll = new LinkedList<Node>();
        ll.add(head);
        Random r = new Random();
        for (int i = 0; i < count; i++)
        {
            Node cur = head;
            while (cur.getSize() > 1 && r.nextInt(100) < passPercent)
                cur = (Node)cur.getArray()[r.nextInt(cur.getSize())];
                Node n = new Node(true);
            ll.add(n);
            cur.add(n);
        }
        return ll;
    }

}