import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import node.*;
import ndfs.*;

public class Test {
    private static final int WARMUPS = 2;
    private static Random r = new Random();

    private static void printUsage() {
    	System.out.println("");
        System.out.println("Usage: bin/runtest <graph_size> <thread_count> <iterations> <num_graphs>");
        System.out.println("  Where:");
        System.out.println("    <graph_size> is the number of nodes in the graph,");
        System.out.println("    <thread_count> is the number of threads,");
        System.out.println("    <iterations> is the number of times to run the test,");
        System.out.println("    <num_graphs> is the number of graphs to generate.");
    	System.out.println("");
    }	
	
    public static void main(String[] args) throws InterruptedException
    {
    	if (args.length < 4) {
    		printUsage();
    		System.out.println("Running default:");
    	}
    	
        int graph_size = args.length > 0 ? Integer.parseInt(args[0]) : 10000;
		int thread_count = args.length > 1 ? Integer.parseInt(args[1]) : 16;
        int iterations = args.length > 2 ? Integer.parseInt(args[2]) : 5;
        int graphs = args.length > 3 ? Integer.parseInt(args[3]) : 500;
        Random r = new Random();


        List<Node> graph;

        for (int k = 0; k < graphs; k++)
        {
            long t1, t2, t3, t4, t5, t6;
            t1=t2=t3=t4=t5=t6=0;
            int keep = 0;
            while (keep == 0)
                keep = r.nextInt(80) - 40;
            graph = genTree(graph_size, keep);

            addEdge(graph, r.nextInt(50)+10);
            addAccept(graph, r.nextInt(30)+5);
            
            for (int i=0; i<WARMUPS; i++) {
		    	run(graph, thread_count);
		    }
        
		    for (int j=0; j<iterations; j++) {
		    	t1+=run(graph, thread_count);
            }
        
		    for (int j=0; j<iterations; j++) {
		    	t2+=run1(graph, thread_count);
            }
        
		    for (int j=0; j<iterations; j++) {
		    	t3+=run4(graph, 1);
            }

        
		    for (int j=0; j<iterations; j++) {
		    	t4+=run4(graph, thread_count);
            }
        
		    for (int j=0; j<iterations; j++) {
		    	t5+=run5(graph, thread_count);
            }
        
		    for (int j=0; j<iterations; j++) {
		    	t6+=run6(graph, thread_count);
            }

            boolean b = run3(graph,1);

            System.out.println("" + b +"," + t3/1.0/iterations +"," + t4/1.0/iterations 
                    +"," + t1/1.0/iterations +"," + t2/1.0/iterations +"," + t5/1.0/iterations
                    +"," + t6/1.0/iterations);
            if (b != run7(graph, thread_count))
                System.out.println("New is bad");
        }
    }

    private static void addAccept(List<Node> graph, int accept) {
        Random r = new Random();
        for (int i = 0; i < (graph.size() / 1000 * accept); i++)
            graph.get(r.nextInt(graph.size())).setAccepting(true);
    }

    private static void addEdge(List<Node> graph, int extra_edge) {
        Random r = new Random();
        for (int i = 0; i < (graph.size() / 100 * extra_edge); i++)
        {
            int a = 0;
            int b = 0;
            while (a == b)
            {
                a = r.nextInt(graph.size());
                b = r.nextInt(graph.size());
            }
            graph.get(a).add(graph.get(b));
        }
    }

    private static long run(List<Node> graph, int thread_count) throws InterruptedException
    {
        MC_NDFS_Thread[] threads = new MC_NDFS_Thread[thread_count];
        MC_NDFS_Thread.reset();
        MC_NDFS_Thread.setup_graph(graph, thread_count);
        AtomicBoolean b = new AtomicBoolean(false);

        for (int i=0; i<thread_count; i++) {
			threads[i] = new MC_NDFS_Thread(graph.get(0), b);
		}
		
		long startTime = System.currentTimeMillis();
		for (int i=0; i<thread_count; i++) {
			threads[i].start();
        }
        
        while (b.get() == false) {}
		long endTime = System.currentTimeMillis();

		boolean hasCycle = false;
		for (int i=0; i<thread_count; i++) {
			threads[i].join();
			hasCycle = hasCycle || threads[i].hasCycle();
		}
		/*
		if (hasCycle) 
			System.out.println("Cycle detected. Runtime = "+(endTime-startTime)+" ms");
		else
            System.out.println("No cycles detected. Runtime = "+(endTime-startTime)+" ms");
            */
        return endTime-startTime;

    }

    private static long run1(List<Node> graph, int thread_count) throws InterruptedException
    {
        MC_NDFS2_Thread[] threads = new MC_NDFS2_Thread[thread_count];
        MC_NDFS2_Thread.reset();
        MC_NDFS2_Thread.setup_graph(graph, thread_count);
        AtomicBoolean b = new AtomicBoolean(false);

        for (int i=0; i<thread_count; i++) {
			threads[i] = new MC_NDFS2_Thread(graph.get(0), b);
		}
		
		long startTime = System.currentTimeMillis();
		for (int i=0; i<thread_count; i++) {
			threads[i].start();
        }
        
        while (b.get() == false) {}
		long endTime = System.currentTimeMillis();

		boolean hasCycle = false;
		for (int i=0; i<thread_count; i++) {
			threads[i].join();
			hasCycle = hasCycle || threads[i].hasCycle();
		}
		/*
		if (hasCycle) 
			System.out.println("Cycle detected. Runtime = "+(endTime-startTime)+" ms");
		else
            System.out.println("No cycles detected. Runtime = "+(endTime-startTime)+" ms");
            */
        return endTime-startTime;

    }

    private static long run2(List<Node> graph, int thread_count) throws InterruptedException
    {
        RB1_Thread[] threads = new RB1_Thread[thread_count];
        RB1_Thread.reset();
        RB1_Thread.setup_graph(graph);
        AtomicBoolean b = new AtomicBoolean(false);

        for (int i=0; i<thread_count; i++) {
			threads[i] = new RB1_Thread(graph.get(0), b);
		}
		
		long startTime = System.currentTimeMillis();
		for (int i=0; i<thread_count; i++) {
			threads[i].start();
        }
        
        while (b.get() == false) {}
		long endTime = System.currentTimeMillis();
        
		boolean hasCycle = false;
		for (int i=0; i<thread_count; i++) {
			threads[i].join();
			hasCycle = hasCycle || threads[i].hasCycle();
		}
		/*
		if (hasCycle) 
			System.out.println("Cycle detected. Runtime = "+(endTime-startTime)+" ms");
		else
            System.out.println("No cycles detected. Runtime = "+(endTime-startTime)+" ms");
            */
        return endTime-startTime;

    }

    private static boolean run3(List<Node> graph, int thread_count) throws InterruptedException
    {
        RB1_Thread[] threads = new RB1_Thread[thread_count];
        RB1_Thread.reset();
        RB1_Thread.setup_graph(graph);
        AtomicBoolean b = new AtomicBoolean(false);

        for (int i=0; i<thread_count; i++) {
			threads[i] = new RB1_Thread(graph.get(0), b);
		}
		
		long startTime = System.currentTimeMillis();
		for (int i=0; i<thread_count; i++) {
			threads[i].start();
        }
        
        while (b.get() == false) {}
		long endTime = System.currentTimeMillis();
        
		boolean hasCycle = false;
		for (int i=0; i<thread_count; i++) {
			threads[i].join();
			hasCycle = hasCycle || threads[i].hasCycle();
		}
		/*
		if (hasCycle) 
			System.out.println("Cycle detected. Runtime = "+(endTime-startTime)+" ms");
		else
            System.out.println("No cycles detected. Runtime = "+(endTime-startTime)+" ms");
            */
        return hasCycle;

    }

    
    private static long run4(List<Node> graph, int thread_count) throws InterruptedException
    {
        RB2_Thread[] threads = new RB2_Thread[thread_count];
        RB2_Thread.reset();
        RB2_Thread.setup_graph(graph, thread_count);
        AtomicBoolean b = new AtomicBoolean(false);

        for (int i=0; i<thread_count; i++) {
			threads[i] = new RB2_Thread(graph.get(0), b);
		}
		
		long startTime = System.currentTimeMillis();
		for (int i=0; i<thread_count; i++) {
			threads[i].start();
        }
        
        while (b.get() == false) {}
		long endTime = System.currentTimeMillis();
        
		boolean hasCycle = false;
		for (int i=0; i<thread_count; i++) {
			threads[i].join();
			hasCycle = hasCycle || threads[i].hasCycle();
		}
        return endTime-startTime;
    }

    
    private static long run5(List<Node> graph, int thread_count) throws InterruptedException
    {
        MC_CNDFS_Thread[] threads = new MC_CNDFS_Thread[thread_count];
        MC_CNDFS_Thread.reset();
        MC_CNDFS_Thread.setup_graph(graph, thread_count);
        AtomicBoolean b = new AtomicBoolean(false);

        for (int i=0; i<thread_count; i++) {
			threads[i] = new MC_CNDFS_Thread(graph.get(0), b);
		}
		
		long startTime = System.currentTimeMillis();
		for (int i=0; i<thread_count; i++) {
			threads[i].start();
        }
        
        while (b.get() == false) {}
		long endTime = System.currentTimeMillis();
        
		boolean hasCycle = false;
		for (int i=0; i<thread_count; i++) {
			threads[i].join();
			hasCycle = hasCycle || threads[i].hasCycle();
		}
        return endTime-startTime;
    }

    private static long run6(List<Node> graph, int thread_count) throws InterruptedException
    {
        NEW_Thread[] threads = new NEW_Thread[thread_count];
        NEW_Thread.reset();
        NEW_Thread.setup_graph(graph, thread_count);
        AtomicBoolean b = new AtomicBoolean(false);

        for (int i=0; i<thread_count; i++) {
			threads[i] = new NEW_Thread(graph.get(0), b);
		}
		
		long startTime = System.currentTimeMillis();
		for (int i=0; i<thread_count; i++) {
			threads[i].start();
        }
        
        while (b.get() == false) {}
		long endTime = System.currentTimeMillis();
        
		boolean hasCycle = false;
		for (int i=0; i<thread_count; i++) {
			threads[i].join();
			hasCycle = hasCycle || threads[i].hasCycle();
		}
        return endTime-startTime;
    }

    private static boolean run7(List<Node> graph, int thread_count) throws InterruptedException
    {
        NEW_Thread[] threads = new NEW_Thread[thread_count];
        NEW_Thread.reset();
        NEW_Thread.setup_graph(graph, thread_count);
        AtomicBoolean b = new AtomicBoolean(false);

        for (int i=0; i<thread_count; i++) {
			threads[i] = new NEW_Thread(graph.get(0), b);
		}
		
		long startTime = System.currentTimeMillis();
		for (int i=0; i<thread_count; i++) {
			threads[i].start();
        }
        
        while (b.get() == false) {}
		long endTime = System.currentTimeMillis();
        
		boolean hasCycle = false;
		for (int i=0; i<thread_count; i++) {
			threads[i].join();
			hasCycle = hasCycle || threads[i].hasCycle();
		}
        return hasCycle;
    }

    // head is at index 0
    public static List<Node> genTree(int count, int keepPercent)
    {
        Node head = new Node(false);
        ArrayList<Node> ll = new ArrayList<Node>(count);
        ll.add(head);
        for (int i = 0; i < count; i++)
        {
            Node cur = head;
            if (keepPercent < 0)
            {
                cur = findNode(head, -keepPercent);
                if (cur == null)
                    cur = head;
            }
            else
                while (cur.getSize() > 1 && r.nextInt(100) > keepPercent)
                    cur = (Node)cur.getArray()[r.nextInt(cur.getSize())];
            Node n = new Node(false);
            ll.add(n);
            cur.add(n);
        }
        return ll;
    }

    
    private static Node findNode(Node head, int keep) {
        Node cur = null;
        if (head.getSize() != 0)
            cur = findNode(head.getArray()[r.nextInt(head.getSize())], keep);
        if (cur == null && r.nextInt(100) < keep)
            return head;
        return cur;
    }

    public static List<Node> genTree2(int count, int passPercent)
    {
        Node head = new Node(false);
        ArrayList<Node> ll = new ArrayList<Node>(count);
        ll.add(head);
        Random r = new Random();
        for (int i = 0; i < count; i++)
        {
            Node cur = ll.get(r.nextInt(ll.size()));
            Node n = new Node(false);
            ll.add(n);
            cur.add(n);
        }
        return ll;
    }

}