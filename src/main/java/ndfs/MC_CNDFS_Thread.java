package ndfs;

import java.util.Iterator;
import java.util.List;

import node.*;

public class MC_CNDFS_Thread extends Thread {
	private static int ID_GEN=0;
	
	private int id;
	private Node graph;

	private static volatile boolean hasCycle = false;
	
	public MC_CNDFS_Thread(Node graph) {
		id = ID_GEN++;
		this.graph = graph;
	}

	public static void setup_graph(List<Node> graph, int n)
	{
		for (Node i : graph)
			i.setup(new MC_NDFSS(n));
	}
	
	public void run() {
		dfs_blue(graph);
	}
	
	public static void reset() {
		ID_GEN=0;
		hasCycle = false;
	}
	
	public boolean hasCycle() {
		return hasCycle;
	}
	
	private void dfs_blue(Node s) {
		if (hasCycle)
			return;
		boolean allred = true;
		((MC_CNDFS)s.getStore()).cyan[id].set(true);
		
		Iterator<Node> subNodes = s.post();
		while (subNodes.hasNext()) {
			Node t = subNodes.next();
			if (!((MC_CNDFS)t.getStore()).cyan[id].get() && !((MC_CNDFS)t.getStore()).blue.get())
				dfs_blue(t);
		}
	}
}
