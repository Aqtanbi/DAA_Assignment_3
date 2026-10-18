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