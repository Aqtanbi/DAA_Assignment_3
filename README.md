# Assignment 3
### Group: SE-2539
### Full name: Aktanbi Kusman
### Course: Design and analysis of algorithms

## Implementation
### Edge and Graph (package graph)

Edge is immutable (from, to, weight, all final) and implements Comparable<Edge> by weight, 
so Kruskal can sort edges directly.

Graph supports both directed and undirected mode. In undirected mode an edge is stored twice 
in the adjacency lists (u→v and v→u) but once in the flat edge list, so Kruskal does not see each road twice.

Vertex indices are validated; a graph must have at least one vertex. Negative weights are not rejected by Graph itself; 
Dijkstra rejects them (see section 4.6).

### Disjoint Set Union (package dsu)

DisjointSet keeps two arrays: parent (the parent of each element, a root points to itself)
and size (the size of each tree, used only for roots).

find uses path compression: after finding the root, every element on the path is
pointed directly at it. It is written iteratively to avoid stack overflow on long chains.

union uses union by size: the smaller tree is attached under the larger one.
It returns true only if two different sets were actually merged.

The class also counts components (starts at n, decreases on every successful merge)
and three operation counters for the experiments: find calls, union calls and parent-pointer steps.
Note that every union calls find twice, and those calls are included in the find counter.

### Kruskal (package mst)

Kruskal sorts a copy of the edge list by weight (the graph itself is not modified,
so the algorithm can be repeated on the same graph) and goes through it with a DisjointSet.
An edge is kept only if union merges two different components. The algorithm stops as soon as
n-1 edges are chosen. If fewer than n-1 edges were chosen, the graph is disconnected and
the result is a spanning forest (connected = false).

Results are returned in MSTResult: chosen edges, total weight, connectivity flag and
operation counters (edge examinations, sort comparisons, DSU operations).

### Prim (package mst)

Prim grows one tree from a start vertex. A PriorityQueue holds the edges leaving the tree;
the cheapest edge is taken, and edges leading to an already visited vertex are skipped
(lazy version, no decrease-key). If the graph is disconnected, Prim restarts from the next
unvisited vertex, so the result is a spanning forest with the same total weight as Kruskal's.

Prim returns the same MSTResult as Kruskal. Edge examinations count all edges looked at
while scanning neighbours, comparisons are counted inside the priority queue comparator,
and DSU operations are 0.