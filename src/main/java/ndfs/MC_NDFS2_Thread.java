package ndfs;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import node.*;

public class MC_NDFS2_Thread extends Thread {
	private static int ID_GEN=0;
	
	private int id;
	private Node graph;

	private static volatile boolean hasCycle = false;
	
	public MC_NDFS2_Thread(Node graph) {
		id = ID_GEN++;
		this.graph = graph;
	}

	public static void setup_graph(List<Node> graph, int n)
	{
		for (Node i : graph)
			i.setup(new MC_NDFS2S(n));
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
		((MC_NDFS2S)s.getStore()).color.set(id, Color.CYAN);
		
		Iterator<Node> subNodes = s.post();
		while (subNodes.hasNext()) {
			Node t = subNodes.next();
			if (((MC_NDFS2S)t.getStore()).color.get(id) == Color.CYAN && (s.isAccepting() || t.isAccepting()))
			{
				hasCycle = true;
				return;
			}
			if (((MC_NDFS2S)t.getStore()).color.get(id) == Color.WHITE && !((MC_NDFS2S)t.getStore()).red.get())
				dfs_blue(t);
			if (!((MC_NDFS2S)t.getStore()).red.get())
				allred = false;
		}
		if (allred)
			((MC_NDFS2S)s.getStore()).red.set(true);
		else if (s.isAccepting()) {
			((MC_NDFS2S)s.getStore()).count.getAndIncrement();
			dfs_red(s);
		}
		((MC_NDFS2S)s.getStore()).color.set(id, Color.BLUE);
	}
	
	private void dfs_red(Node s) {
		if (hasCycle)
			return;
		((MC_NDFS2S)s.getStore()).color.set(id, Color.PINK);
		Iterator<Node> subNodes = s.post();
		while (subNodes.hasNext()) {
			Node t = subNodes.next();
			if (((MC_NDFS2S)t.getStore()).color.get(id) == Color.CYAN) {
				hasCycle = true;
				return;
			}
			if (((MC_NDFS2S)t.getStore()).color.get(id) != Color.PINK && !((MC_NDFS2S)t.getStore()).red.get())
				dfs_red(t);
		}
		if (s.isAccepting()) {
			((MC_NDFS2S)s.getStore()).count.getAndDecrement();
			AtomicInteger target = ((MC_NDFS2S)s.getStore()).count;
			while (target.get() > 0) {}
		}
		((MC_NDFS2S)s.getStore()).red.set(true);
		
	}
}
