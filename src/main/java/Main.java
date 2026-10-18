import graph.Graph;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
public class Main {
    public static void main(String[] args) {
        Graph g = new Graph(9, false);
        g.addEdge(6, 7, 1);  g.addEdge(2, 8, 2);  g.addEdge(5, 6, 2);
        g.addEdge(0, 1, 4);  g.addEdge(2, 5, 4);  g.addEdge(6, 8, 6);
        g.addEdge(2, 3, 7);  g.addEdge(7, 8, 7);  g.addEdge(0, 7, 8);
        g.addEdge(1, 2, 8);  g.addEdge(3, 4, 9);  g.addEdge(4, 5, 10);
        g.addEdge(1, 7, 11); g.addEdge(3, 5, 14);
        Graph d = new Graph(4, false);            // undirected graph
        d.addEdge(0, 1, 5);
        d.addEdge(2, 3, 3);
        MSTResult r2 = Kruskal.run(d);
        System.out.println(r2.connected);         // false
        System.out.println(r2.totalWeight);       // 8

        MSTResult k = Kruskal.run(g);
        MSTResult p = Prim.run(g);
        System.out.println(p.totalWeight);                    // 37
        System.out.println(p.edges.size());                   // 8
        System.out.println(p.connected);                      // true
        System.out.println(k.totalWeight == p.totalWeight);   // true

        MSTResult p2 = Prim.run(d);
        System.out.println(p2.connected);                     // false
        System.out.println(p2.totalWeight);                   // 8
    }
}