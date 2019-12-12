package ndfs;

import java.util.HashMap;
import java.util.Map;

import node.Node;


public class ColorMap {
	public static enum Color {
		WHITE, CYAN, BLUE, RED
	}
	
	private Map<Node, Color>colorMap;
	
	public ColorMap() {
		colorMap = new HashMap();
	}
	
	public boolean checkColor(Node s, Color color) {
		/** Nodes begin as white.
		 *  An untraversed node wouldn't have been entered into the colormap. */
		if (color == Color.WHITE)  {
			return colorMap.containsKey(s);
		}
		return (colorMap.get(s) == color);
	}
	
	public void color(Node s, Color color) {
		/** Sanity check: if the color parameter is white,
		 *  remove the node from the colormap. */
		if (color == Color.WHITE) {
			colorMap.remove(s);
		} else {
			colorMap.put(s, color);
		}
	}
}
