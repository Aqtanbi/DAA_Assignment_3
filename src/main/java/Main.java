import connectivity.TopoResult;
import connectivity.TopologicalSort;
import graph.Graph;

public class Main {
    public static void main(String[] args) {
        Graph dag = new Graph(4, true);
        dag.addEdge(0, 1, 1);
        dag.addEdge(0, 2, 1);
        dag.addEdge(1, 3, 1);
        dag.addEdge(2, 3, 1);

        TopoResult r = TopologicalSort.run(dag);
        System.out.println(r.isDag);                                       // true
        System.out.println(r.order);                                       // [0, 1, 2, 3]
        System.out.println(TopologicalSort.isValidOrder(dag, r.order));    // true

        Graph cyc = new Graph(4, true);
        cyc.addEdge(0, 1, 1);
        cyc.addEdge(1, 2, 1);
        cyc.addEdge(2, 1, 1);          // cycle 1 -> 2 -> 1
        cyc.addEdge(2, 3, 1);

        TopoResult c = TopologicalSort.run(cyc);
        System.out.println(c.isDag);                                       // false
        System.out.println(c.order);                                       // [0]

        Graph one = new Graph(1, true);
        TopoResult s = TopologicalSort.run(one);
        System.out.println(s.isDag + " " + s.order);                       // true [0]
    }
}