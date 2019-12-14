# Multicore_Nested_Depth-First_Search

To compile, run: ./gradlew build
To run: ./bin/runTest <graph_size> <thread_count> <iterations> <num_graphs>

This project runs microbenchamrk on multicore DFS algorithms.

The microbenchmark randomly generates graphs, which has
fixed number of nodes, <graph_size>.
The randomd graph generation takes 3 steps:
generate tree, make random connection, and mark random node.

In tree generation, a random level-to-level ratio is used, and
the algorithm generates a tree with that ratio.
A tree has no cycle, so we add additional edges in the seccond step.
We increase edge count by a random fraction, so 
there can be more or none cycles.
In the end, we mark a random fraction of nodes accepting,
which the algorithm only deals with cycles including an accepting node.

The DFS algorithm we used are from papar Multicore
Nested Depth-First Search, and paper Improved Multicore
Nested Depth-First Search.
All of their algorithm is implemented, with a additional one, 
NEW.

The report of this project is also included with LaTeX source code, source files, and produced PDF, in folder report.

