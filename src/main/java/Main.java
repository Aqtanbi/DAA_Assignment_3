import graph.Graph;

public class Main {
    static void main(String[] args) {
        Graph g = new Graph(4, false);
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 3);
        System.out.println(g.getEdgeCount());
        System.out.println(g.getNeighbors(1));
    }
}
