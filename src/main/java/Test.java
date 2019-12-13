import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import node.*;
import ndfs.*;

public class Test {
	private static final int WARMUPS = 2;

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
    		System.exit(1);
    	}
    	
        int graph_size = Integer.parseInt(args[0]);
		int thread_count = Integer.parseInt(args[1]);
        int iterations = Integer.parseInt(args[2]);
        int graphs = Integer.parseInt(args[3]);
        //int graph_size = 30000;
        //int thread_count = 2;
        //int iterations = 3;
        int extra_edge = 30;
        int accept = 5;


        List<Node> graph;

        long total01, total02, total03;
        total01=total02=total03=0;
        long total11, total12, total13;
        total11=total12=total13=0;
        int count = 0;

        for (int k = 0; k < graphs; k++)
        {
            long t1, t2, t3;
            t1=t2=t3=0;
            graph = genTree(graph_size, 70);

            addEdge(graph, extra_edge);
            addAccept(graph, accept);

            for (int i=0; i<WARMUPS; i++) {
		    	run(graph, thread_count);
		    }
        
		    for (int j=0; j<iterations; j++) {
		    	t1+=run(graph, thread_count);
            }

            for (int i=0; i<WARMUPS; i++) {
		    	run1(graph, thread_count);
		    }
        
		    for (int j=0; j<iterations; j++) {
		    	t2+=run1(graph, thread_count);
            }

            for (int i=0; i<WARMUPS; i++) {
		    	run2(graph, 1);
		    }
        
		    for (int j=0; j<iterations; j++) {
		    	t3+=run2(graph, 1);
            }

            if (run3(graph,1))
            {
                total01 += t1;
                total02 += t2;
                total03 += t3;
                count++;
            }
            else
            {
                total11 += t1;
                total12 += t2;
                total13 += t3;
            }
        }
        System.out.println(total01/1.0/count+","+total02/1.0/count+","+total03/1.0/count);
        System.out.println(total11/1.0/(graphs - count)+","+total12/1.0/(graphs - count)+","+total13/1.0/(graphs - count));
/*  
        System.out.println(" ");
        for (int j = 0; j < 100; j++)
        {
            graph = genTree(graph_size, 70);
            addEdge(graph, extra_edge);
            run1(graph, thread_count);
        }
        */
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
		/*
		if (hasCycle) 
			System.out.println("Cycle detected. Runtime = "+(endTime-startTime)+" ms");
		else
            System.out.println("No cycles detected. Runtime = "+(endTime-startTime)+" ms");
            */
        return hasCycle;

    }

    // head is at index 0
    public static List<Node> genTree(int count, int passPercent)
    {
        Node head = new Node(false);
        ArrayList<Node> ll = new ArrayList<Node>(count);
        ll.add(head);
        Random r = new Random();
        for (int i = 0; i < count; i++)
        {
            Node cur = head;
            while (cur.getSize() > 1 && r.nextInt(100) < passPercent)
                cur = (Node)cur.getArray()[r.nextInt(cur.getSize())];
                Node n = new Node(false);
            ll.add(n);
            cur.add(n);
        }
        return ll;
    }

}