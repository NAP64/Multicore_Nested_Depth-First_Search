package bench;

import java.util.Map;
import java.util.HashMap;

import node.*;
import ndfs.*;

public class MC_NDFS {
	private static final int WARMUPS = 2;
	
	private static Node graph=null;
	private static Map<Node, Boolean> global_red_map;
	private static Map<Node, Integer> global_count_map;
	
	public static void main(String[] args) throws Exception {
		// get command line arguments
		int graph_size = Integer.parseInt(args[0]);
		int thread_count = Integer.parseInt(args[1]);
		int iterations = Integer.parseInt(args[2]);

		// warmup section
		for (int i=0; i<WARMUPS; i++) {
			run(graph_size, thread_count);
		}
		
		for (int j=0; j<iterations; j++) {
			run(graph_size, thread_count);
		}
	}
	
	private static void run(int graph_size, int thread_count) throws InterruptedException {
		// graph = createGraph();
		global_red_map = new HashMap();
		global_count_map = new HashMap();
		
		NDFS_Thread[] threads = new NDFS_Thread[thread_count];
		
		NDFS_Thread.reset();
		
		for (int i=0; i<thread_count; i++) {
			threads[i] = new NDFS_Thread(graph, global_red_map, global_count_map);
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
}
