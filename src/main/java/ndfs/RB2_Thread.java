package ndfs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import node.*;

public class RB2_Thread extends Thread {
	private static int ID_GEN=0;
	
	private int id;
	private Node graph;
	private AtomicBoolean finished;
	ThreadLocal<HashMap<Node, Color>> h;

	private static volatile boolean hasCycle = false;
	
	public RB2_Thread(Node graph, AtomicBoolean b) {
		id = ID_GEN++;
		this.graph = graph;
		h = ThreadLocal.withInitial(new Supplier<HashMap<Node, Color>>() {
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
			i.setup(new RB2Store(n));
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
		h.get().put(s, Color.CYAN);
		Iterator<Node> it = s.post();
		while (it.hasNext())
		{
			Node cur = (Node)it.next();
			if (!h.get().containsKey(cur))
				dfs_blue(cur);
		}
		if (s.isAccepting())
		{
			dfs_red(s);
			h.get().put(s, Color.RED);
		}
		else
			h.get().put(s, Color.BLUE);
	}
	
	private void dfs_red(Node s) {
		if (hasCycle)
			return;
		Iterator<Node> it = s.post();
		while (it.hasNext())
		{
			Node cur = (Node)it.next();
			if (h.get().containsKey(cur) && h.get().get(cur) == Color.CYAN)
				hasCycle = true;
			else if (h.get().containsKey(cur) && h.get().get(cur) == Color.BLUE)
			{
				h.get().put(s, Color.RED);
				dfs_red(cur);
			}
		}
	}
}
