package graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int  vertices;
    private final boolean directed;
    private final List<List<Edge>> adj;
    private final List<Edge> edges;

    public Graph(int vertices, boolean directed){
        if (vertices < 1){
            throw new IllegalArgumentException("Graph must have aat least 1 vertex");
        }
        this.vertices = vertices;
        this.directed = directed;
        this.adj = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adj.add(new ArrayList<>());
        }
        this.edges = new ArrayList<>();
    }

    public void addEdge(int u, int v, int weight){
        checkVertex(u);
        checkVertex(v);
        Edge e = new Edge(u, v, weight);
        edges.add(e);
        adj.get(u).add(e);
        if (!directed) {
            adj.get(v).add(new Edge(v, u, weight));
        }
    }
    public int getVertexCount(){return vertices;}
    public int getEdgeCount(){return edges.size();}
    public boolean isDirected(){return directed;}
    public List<Edge> getNeighbors(int v) {return adj.get(v);}
    public List<Edge> getEdges(){return edges;}

    private void checkVertex(int v){
        if (v < 0 || v >= vertices) {
            throw new IllegalArgumentException("Vertex out of range: " + v);

        }
    }
}