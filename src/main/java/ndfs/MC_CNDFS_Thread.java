package ndfs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import node.*;

public class MC_CNDFS_Thread extends Thread {
	private static int ID_GEN=0;
	
	private int id;
	private Node graph;
	private AtomicBoolean finished;
	ThreadLocal<HashSet<Node>> cyan;

	private static volatile boolean hasCycle = false;
	
	public MC_CNDFS_Thread(Node graph, AtomicBoolean b) {
		id = ID_GEN++;
		this.graph = graph;
		cyan = ThreadLocal.withInitial(new Supplier<HashSet<Node>>() {
			@Override
			public HashSet<Node> get() {
				return new HashSet<Node>();
			}
		});
		finished = b;
	}

	public static void setup_graph(List<Node> graph, int n)
	{
		for (Node i : graph)
			i.setup(new MC_CNDFS(n));
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
		cyan.get().add(s);
		
		Iterator<Node> subNodes = s.post();
		while (subNodes.hasNext()) {
			Node t = subNodes.next();
			if (!cyan.get().contains(t) && !((MC_CNDFS)t.getStore()).blue.get())
				dfs_blue(t);
		}
		((MC_CNDFS)s.getStore()).blue.set(true);
		if (s.isAccepting())
		{
			HashSet<Node> h = new HashSet<Node>();
			dfs_red(s, h);
			Iterator<Node> it = h.iterator();
			while (it.hasNext() && !hasCycle)
			{
				Node s1 = it.next();
				while (!hasCycle && (s1 != s && s1.isAccepting() && !((MC_CNDFS)(s1.getStore())).red.get())) {}
			}
			it = h.iterator();
			while (it.hasNext() && !hasCycle)
				((MC_CNDFS)(it.next().getStore())).red.set(true);
		}
		cyan.get().remove(s);
	}

	private void dfs_red(Node s, HashSet<Node> h) {
		if (hasCycle)
			return;
		h.add(s);
	
		Iterator<Node> subNodes = s.post();
		while (subNodes.hasNext()) {
			Node t = subNodes.next();
			if (cyan.get().contains(t))
			{
				hasCycle = true;
				return;
			}
			if (!h.contains(t) && !((MC_CNDFS)(t.getStore())).red.get())
				dfs_red(t, h);
		}
	}
}
