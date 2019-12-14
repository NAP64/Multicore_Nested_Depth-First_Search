package ndfs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import node.*;

public class MC_NDFS2_Thread extends Thread {
	private static int ID_GEN=0;
	
	private int id;
	private Node graph;
	private AtomicBoolean finished;
	ThreadLocal<HashMap<Node, Color>> color;

	private static volatile boolean hasCycle = false;
	
	public MC_NDFS2_Thread(Node graph, AtomicBoolean b) {
		id = ID_GEN++;
		this.graph = graph;
		color = ThreadLocal.withInitial(new Supplier<HashMap<Node, Color>>() {
			@Override
			public HashMap<Node, Color> get() {
				return new HashMap<Node, Color>();
			}
		});
		finished = b;
	}

	public static void setup_graph(List<Node> graph, int n)
	{
		for (Node i : graph)
			i.setup(new MC_NDFS2S(n));
	}
	
	public void run() {
		dfs_blue(graph);
		finished.set(true);
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
		color.get().put(s, Color.CYAN);
		
		Iterator<Node> subNodes = s.post();
		while (subNodes.hasNext()) {
			Node t = subNodes.next();
			if (color.get().containsKey(t) && color.get().get(t) == Color.CYAN && (s.isAccepting() || t.isAccepting()))
			{
				hasCycle = true;
				return;
			}
			if (!color.get().containsKey(t) && !((MC_NDFS2S)t.getStore()).red.get())
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
		color.get().put(s, Color.BLUE);
	}
	
	private void dfs_red(Node s) {
		if (hasCycle)
			return;
		color.get().put(s, Color.PINK);
		Iterator<Node> subNodes = s.post();
		while (subNodes.hasNext()) {
			Node t = subNodes.next();
			if (color.get().containsKey(t) && color.get().get(t) == Color.CYAN) {
				hasCycle = true;
				return;
			}
			if (color.get().containsKey(t) && color.get().get(t) != Color.PINK && !((MC_NDFS2S)t.getStore()).red.get())
				dfs_red(t);
		}
		if (s.isAccepting()) {
			((MC_NDFS2S)s.getStore()).count.getAndDecrement();
			AtomicInteger target = ((MC_NDFS2S)s.getStore()).count;
			while (target.get() > 0 && !hasCycle) {}
		}
		((MC_NDFS2S)s.getStore()).red.set(true);
		
	}
}
