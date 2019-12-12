package ndfs;

import java.util.Iterator;
import java.util.List;

import node.*;

public class RB1_Thread extends Thread {
	private static int ID_GEN=0;
	
	private int id;
	private Node graph;

	private static volatile boolean hasCycle = false;
	
	public RB1_Thread(Node graph) {
		id = ID_GEN++;
		this.graph = graph;
	}

	public static void setup_graph(List<Node> graph)
	{
		for (Node i : graph)
			i.setup(new RB1Store());
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
		((RB1Store)s.getStore()).c.set(Color.CYAN);
		Iterator<Node> it = s.post();
		while (it.hasNext())
		{
			Node cur = (Node)it.next();
			if (((RB1Store)cur.getStore()).c.get() == Color.WHITE)
				dfs_blue(cur);
		}
		if (s.isAccepting())
		{
			dfs_red(s);
			((RB1Store)s.getStore()).c.set(Color.RED);
		}
		else
			((RB1Store)s.getStore()).c.set(Color.BLUE);
	}
	
	private void dfs_red(Node s) {
		if (hasCycle)
			return;
		Iterator<Node> it = s.post();
		while (it.hasNext())
		{
			Node cur = (Node)it.next();
			if (((RB1Store)cur.getStore()).c.get() == Color.CYAN)
				hasCycle = true;
			else if (((RB1Store)cur.getStore()).c.get() == Color.BLUE)
			{
				((RB1Store)cur.getStore()).c.set(Color.RED);
				dfs_red(cur);
			}
		}
	}
}
