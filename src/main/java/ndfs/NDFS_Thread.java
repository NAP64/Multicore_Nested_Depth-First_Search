package ndfs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import node.Node;

public class NDFS_Thread extends Thread {
	private static int ID_GEN=0;
	
	private int id;
	private Node graph;
	private boolean hasCycle;
	private ColorMap colorMap;
	private Map<Node, Boolean> pink_map;
	private Map<Node, Boolean> global_red_map;
	private Map<Node, Integer> global_count_map;
	
	public NDFS_Thread(Node graph, Map<Node, Boolean> global_red_map, Map<Node, Integer> global_count_map) {
		id = ID_GEN++;
		this.graph = graph;
		this.pink_map = new HashMap();
		this.colorMap = new ColorMap();
		this.global_red_map = global_red_map;
		this.global_count_map = global_count_map;
		this.hasCycle = false;
	}
	
	public void run() {
		try {
			// no exception, no cycle
			dfs_blue(graph);
		} catch (CYCLE_FOUND_EXCEPTION e) {
			hasCycle = true;
		} catch (InterruptedException e) {
		}
	}
	
	public static void reset() {
		ID_GEN=0;
	}
	
	public boolean hasCycle() {
		return hasCycle;
	}
	
	private synchronized boolean is_red(Node s) {
		return (global_red_map.containsKey(s));
	}
	
	private synchronized void mod_count(Node s, int i) {
		if (!global_count_map.containsKey(s)) {
			global_count_map.put(s, i);
		} else {
			global_count_map.put(s, global_count_map.get(s)+i);
		}
	}
	
	private void color_pink(Node s) {
		pink_map.put(s, true);
	}
	
	private boolean is_pink(Node s) {
		return pink_map.containsKey(s);
	}
	
	private void dfs_blue(Node s) throws CYCLE_FOUND_EXCEPTION, InterruptedException {
		colorMap.color(s, ColorMap.Color.CYAN);
		
		Iterator<Node> subNodes = s.post();
		while (subNodes.hasNext()) {
			Node t = subNodes.next();
			boolean isRed;
			synchronized (global_red_map) {
				Boolean b = global_red_map.get(t);
				isRed = (b == null) ? false : true;
			}
			if (colorMap.checkColor(t, ColorMap.Color.WHITE) && !isRed) {
				dfs_blue(t);
			}
		}
		if (s.isAccepting()) {
			synchronized (global_count_map) {
				Integer i = global_count_map.get(s);
				int j = (i==null) ? 0 : i;
				global_count_map.put(s, j++);
			}
			dfs_red(s);
		}
		colorMap.color(s, ColorMap.Color.BLUE);
	}
	
	private void dfs_red(Node s) throws CYCLE_FOUND_EXCEPTION {
		color_pink(s);
		Iterator<Node> subNodes = s.post();
		while (subNodes.hasNext()) {
			Node t = subNodes.next();
			if (colorMap.checkColor(t, ColorMap.Color.CYAN)) {
				throw new CYCLE_FOUND_EXCEPTION();
			}
			boolean isRed;
			synchronized (global_red_map) {
				Boolean b = global_red_map.get(t);
				isRed = (b == null) ? false : true;
			}
			if (!is_pink(t) && !isRed) {
				dfs_red(t);
			}
			if (s.isAccepting()) {
				synchronized (global_count_map) {
					Integer i = global_count_map.get(s);
					int j = (i==null) ? 0 : i;
					global_count_map.put(s,  j--);
					// wait until global count is 0...
				}
				synchronized (global_count_map) {
					while (true) {
						Integer i = global_count_map.get(s);
						int j = (i==null) ? 0 : i;
						// if count is 0 or less, we're done, else we wait()
						if (j > 0) {
							try {
								global_count_map.wait();
							} catch (InterruptedException e) {
								System.out.println(e);
							}
						} else {
							break;
						}
					}
					global_count_map.notifyAll(); // wake up any waiting threads
				}
				synchronized (global_red_map) {
					global_red_map.put(s, true);
				}
				pink_map.remove(s);
			}
		}
	}
}
