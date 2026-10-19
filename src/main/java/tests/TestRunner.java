package tests;

import connectivity.SCCResult;
import connectivity.TarjanSCC;
import connectivity.TopoResult;
import connectivity.TopologicalSort;
import dsu.DisjointSet;
import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;
import shortestpath.DijkstraResult;

import java.util.Arrays;
import java.util.List;

public class TestRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testDsu();
        testLectureGraphMst();
        testDisconnectedMst();
        testMultipleValidMsts();
        testDuplicateWeights();
        testSingleVertex();
        testTopologicalDag();
        testTopologicalCycle();
        testScc();
        testDijkstraBasic();
        testDijkstraUnreachable();
        testDijkstraNegativeWeight();
        testWrongGraphType();
        testRandomKruskalVsPrim();
        testRandomTopological();
        testRandomScc();
        testRandomDijkstraVsBellmanFord();
        testGeneratorReproducible();

        System.out.println();
        System.out.println("Passed: " + passed + ", failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }


    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    private static boolean throwsIllegalArgument(Runnable r) {
        try {
            r.run();
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private static Graph lectureGraph() {
        Graph g = new Graph(9, false);
        g.addEdge(6, 7, 1);  g.addEdge(2, 8, 2);  g.addEdge(5, 6, 2);
        g.addEdge(0, 1, 4);  g.addEdge(2, 5, 4);  g.addEdge(6, 8, 6);
        g.addEdge(2, 3, 7);  g.addEdge(7, 8, 7);  g.addEdge(0, 7, 8);
        g.addEdge(1, 2, 8);  g.addEdge(3, 4, 9);  g.addEdge(4, 5, 10);
        g.addEdge(1, 7, 11); g.addEdge(3, 5, 14);
        return g;
    }

    private static long[] bellmanFord(Graph g, int source) {
        int n = g.getVertexCount();
        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[source] = 0;
        for (int i = 0; i < n - 1; i++) {
            for (Edge e : g.getEdges()) {
                if (dist[e.from] != Long.MAX_VALUE && dist[e.from] + e.weight < dist[e.to]) {
                    dist[e.to] = dist[e.from] + e.weight;
                }
                if (!g.isDirected() && dist[e.to] != Long.MAX_VALUE
                        && dist[e.to] + e.weight < dist[e.from]) {
                    dist[e.from] = dist[e.to] + e.weight;
                }
            }
        }
        return dist;
    }

    private static void testDsu() {
        DisjointSet ds = new DisjointSet(5);
        check("DSU: first union merges", ds.union(0, 1));
        check("DSU: second union merges", ds.union(2, 3));
        check("DSU: repeated union returns false", !ds.union(1, 0));
        check("DSU: union of two groups merges", ds.union(1, 3));
        check("DSU: components counted correctly", ds.getComponents() == 2);
        check("DSU: connected elements", ds.connected(0, 3));
        check("DSU: separate elements", !ds.connected(4, 0));
        check("DSU: needs at least one element", throwsIllegalArgument(() -> new DisjointSet(0)));
    }


    private static void testLectureGraphMst() {
        Graph g = lectureGraph();
        MSTResult k = Kruskal.run(g);
        MSTResult p = Prim.run(g);
        check("MST small graph: Kruskal weight 37", k.totalWeight == 37);
        check("MST small graph: Prim weight 37", p.totalWeight == 37);
        check("MST small graph: Kruskal uses 8 edges", k.edges.size() == 8);
        check("MST small graph: Prim uses 8 edges", p.edges.size() == 8);
        check("MST small graph: connected", k.connected && p.connected);
        check("MST small graph: Kruskal examined 11 edges (as in lecture)",
                k.edgeExaminations == 11);
    }

    private static void testDisconnectedMst() {
        Graph g = new Graph(5, false);
        g.addEdge(0, 1, 5);
        g.addEdge(2, 3, 3);
        MSTResult k = Kruskal.run(g);
        MSTResult p = Prim.run(g);
        check("Disconnected: Kruskal detects it", !k.connected);
        check("Disconnected: Prim detects it", !p.connected);
        check("Disconnected: same forest weight", k.totalWeight == 8 && p.totalWeight == 8);
    }

    private static void testMultipleValidMsts() {
        Graph g = new Graph(3, false);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(0, 2, 1);
        MSTResult k = Kruskal.run(g);
        MSTResult p = Prim.run(g);
        check("Multiple MSTs: weights equal", k.totalWeight == 2 && p.totalWeight == 2);
        check("Multiple MSTs: both use 2 edges", k.edges.size() == 2 && p.edges.size() == 2);
    }

    private static void testDuplicateWeights() {
        Graph g = new Graph(6, false);
        g.addEdge(0, 1, 5); g.addEdge(1, 2, 5);
        g.addEdge(3, 4, 5); g.addEdge(4, 5, 5);
        g.addEdge(0, 3, 5); g.addEdge(1, 4, 5); g.addEdge(2, 5, 5);
        MSTResult k = Kruskal.run(g);
        MSTResult p = Prim.run(g);
        check("Duplicate weights: MST weight is 25", k.totalWeight == 25 && p.totalWeight == 25);
    }

    private static void testSingleVertex() {
        Graph u = new Graph(1, false);
        MSTResult k = Kruskal.run(u);
        MSTResult p = Prim.run(u);
        check("Single vertex: MST weight 0, connected",
                k.totalWeight == 0 && p.totalWeight == 0 && k.connected && p.connected);

        Graph d = new Graph(1, true);
        TopoResult t = TopologicalSort.run(d);
        check("Single vertex: topological order [0]", t.isDag && t.order.equals(List.of(0)));
        SCCResult s = TarjanSCC.run(d);
        check("Single vertex: one SCC", s.count == 1);
        DijkstraResult r = Dijkstra.run(d, 0);
        check("Single vertex: Dijkstra distance 0", r.dist[0] == 0 && r.reachableCount() == 1);
    }

    private static void testTopologicalDag() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 1);
        TopoResult r = TopologicalSort.run(g);
        check("Topological: DAG recognised", r.isDag);
        check("Topological: order is valid", TopologicalSort.isValidOrder(g, r.order));
        check("Topological: invalid order is rejected",
                !TopologicalSort.isValidOrder(g, Arrays.asList(3, 2, 1, 0)));
    }

    private static void testTopologicalCycle() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 1, 1);
        g.addEdge(2, 3, 1);
        TopoResult r = TopologicalSort.run(g);
        check("Topological: cycle detected", !r.isDag);
        check("Topological: partial order is not a full order", r.order.size() < 4);
    }


    private static void testScc() {
        Graph g = new Graph(6, true);
        g.addEdge(0, 1, 1); g.addEdge(1, 2, 1); g.addEdge(2, 0, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 4, 1); g.addEdge(4, 5, 1); g.addEdge(5, 3, 1);
        SCCResult r = TarjanSCC.run(g);
        check("SCC: two components", r.count == 2);
        check("SCC: 0,1,2 together", r.componentId[0] == r.componentId[1]
                && r.componentId[1] == r.componentId[2]);
        check("SCC: 3,4,5 together", r.componentId[3] == r.componentId[4]
                && r.componentId[4] == r.componentId[5]);
        check("SCC: the two groups are different", r.componentId[0] != r.componentId[3]);

        Graph dag = new Graph(4, true);
        dag.addEdge(0, 1, 1); dag.addEdge(1, 2, 1); dag.addEdge(2, 3, 1);
        check("SCC: DAG gives one component per vertex", TarjanSCC.run(dag).count == 4);
    }

    private static void testDijkstraBasic() {
        Graph g = new Graph(5, true);
        g.addEdge(0, 1, 10);
        g.addEdge(0, 2, 3);
        g.addEdge(2, 1, 4);
        g.addEdge(1, 3, 2);
        g.addEdge(2, 3, 8);
        g.addEdge(3, 4, 5);
        DijkstraResult r = Dijkstra.run(g, 0);
        check("Dijkstra: dist to 1 is 7", r.dist[1] == 7);
        check("Dijkstra: dist to 3 is 9", r.dist[3] == 9);
        check("Dijkstra: dist to 4 is 14", r.dist[4] == 14);
        check("Dijkstra: path to 4", r.getPath(4).equals(List.of(0, 2, 1, 3, 4)));
        check("Dijkstra: path to source", r.getPath(0).equals(List.of(0)));
    }

    private static void testDijkstraUnreachable() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 2);
        g.addEdge(2, 3, 1);
        DijkstraResult r = Dijkstra.run(g, 0);
        check("Dijkstra: unreachable vertex detected", !r.isReachable(2) && !r.isReachable(3));
        check("Dijkstra: unreachable path is empty", r.getPath(3).isEmpty());
        check("Dijkstra: reachable count is 2", r.reachableCount() == 2);
    }

    private static void testDijkstraNegativeWeight() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 2);
        g.addEdge(0, 2, 5);
        g.addEdge(2, 1, -4);
        check("Dijkstra: negative weight is rejected",
                throwsIllegalArgument(() -> Dijkstra.run(g, 0)));
        check("Dijkstra: bad source is rejected",
                throwsIllegalArgument(() -> Dijkstra.run(new Graph(2, true), 5)));
    }

    private static void testWrongGraphType() {
        Graph dir = new Graph(2, true);
        Graph und = new Graph(2, false);
        check("Kruskal rejects directed graph", throwsIllegalArgument(() -> Kruskal.run(dir)));
        check("Prim rejects directed graph", throwsIllegalArgument(() -> Prim.run(dir)));
        check("Topological sort rejects undirected graph",
                throwsIllegalArgument(() -> TopologicalSort.run(und)));
        check("SCC rejects undirected graph", throwsIllegalArgument(() -> TarjanSCC.run(und)));
    }

    private static void testRandomKruskalVsPrim() {
        boolean ok = true;
        int[][] sizes = {{10, 30}, {30, 100}, {100, 500}, {200, 400}, {50, 49}};
        for (int[] s : sizes) {
            for (long seed = 1; seed <= 20; seed++) {
                Graph g = GraphGenerator.connectedUndirected(s[0], s[1], seed, 50);
                MSTResult k = Kruskal.run(g);
                MSTResult p = Prim.run(g);
                if (k.totalWeight != p.totalWeight || !k.connected || !p.connected
                        || k.edges.size() != s[0] - 1 || p.edges.size() != s[0] - 1) {
                    ok = false;
                }
            }
        }
        check("Random: Kruskal and Prim give the same weight (100 graphs)", ok);
    }

    private static void testRandomTopological() {
        boolean dagOk = true;
        boolean cycleOk = true;
        for (long seed = 1; seed <= 50; seed++) {
            Graph dag = GraphGenerator.randomDag(60, 200, seed, 10);
            TopoResult r = TopologicalSort.run(dag);
            if (!r.isDag || !TopologicalSort.isValidOrder(dag, r.order)) {
                dagOk = false;
            }
            Graph cyc = GraphGenerator.randomDirected(60, 300, seed, 10);
            if (TopologicalSort.run(cyc).isDag == (TarjanSCC.run(cyc).count == 60)) {
                // both methods must agree on whether the graph has a cycle
            } else {
                cycleOk = false;
            }
        }
        check("Random: every DAG gets a valid topological order (50 graphs)", dagOk);
        check("Random: cycle detection agrees with SCC (50 graphs)", cycleOk);
    }

    private static void testRandomScc() {
        boolean ok = true;
        for (long seed = 1; seed <= 50; seed++) {
            Graph g = GraphGenerator.randomDirected(80, 160, seed, 10);
            SCCResult r = TarjanSCC.run(g);
            int total = 0;
            boolean[] seen = new boolean[80];
            for (List<Integer> comp : r.components) {
                total += comp.size();
                for (int v : comp) {
                    if (seen[v]) {
                        ok = false;      // vertex in two components
                    }
                    seen[v] = true;
                }
            }
            if (total != 80) {
                ok = false;
            }
            for (int id : r.componentId) {
                if (id < 0 || id >= r.count) {
                    ok = false;
                }
            }
        }
        check("Random: every vertex is in exactly one SCC (50 graphs)", ok);
    }

    private static void testRandomDijkstraVsBellmanFord() {
        boolean ok = true;
        for (long seed = 1; seed <= 30; seed++) {
            Graph d = GraphGenerator.randomDirected(60, 150, seed, 20);
            Graph u = GraphGenerator.connectedUndirected(60, 150, seed, 20);
            for (Graph g : new Graph[]{d, u}) {
                long[] expected = bellmanFord(g, 0);
                long[] actual = Dijkstra.run(g, 0).dist;
                if (!Arrays.equals(expected, actual)) {
                    ok = false;
                }
            }
        }
        check("Random: Dijkstra equals Bellman-Ford (60 graphs)", ok);
    }

    private static void testGeneratorReproducible() {
        Graph a = GraphGenerator.connectedUndirected(100, 500, 42, 100);
        Graph b = GraphGenerator.connectedUndirected(100, 500, 42, 100);
        boolean same = a.getEdgeCount() == 500 && b.getEdgeCount() == 500;
        for (int i = 0; same && i < 500; i++) {
            Edge x = a.getEdges().get(i);
            Edge y = b.getEdges().get(i);
            same = x.from == y.from && x.to == y.to && x.weight == y.weight;
        }
        check("Generator: same seed gives the same graph", same);
        check("Generator: generated graph is connected", Kruskal.run(a).connected);
    }
}
