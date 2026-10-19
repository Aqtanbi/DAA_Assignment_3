package graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GraphGenerator {

    public static Graph connectedUndirected(int n, int m, long seed, int maxWeight) {
        long maxEdges = (long) n * (n - 1) / 2;
        if (m < n - 1 || m > maxEdges) {
            throw new IllegalArgumentException(
                    "Connected undirected graph needs " + (n - 1) + " <= m <= " + maxEdges);
        }
        Random rnd = new Random(seed);
        Graph g = new Graph(n, false);
        Set<Long> used = new HashSet<>();

        // Step 1: random spanning tree -> guarantees connectivity.
        for (int v = 1; v < n; v++) {
            int u = rnd.nextInt(v);
            used.add(undirectedKey(u, v, n));
            g.addEdge(u, v, 1 + rnd.nextInt(maxWeight));
        }
        // Step 2: extra random edges until m edges exist.
        while (g.getEdgeCount() < m) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            if (u == v || !used.add(undirectedKey(u, v, n))) {
                continue;
            }
            g.addEdge(u, v, 1 + rnd.nextInt(maxWeight));
        }
        return g;
    }

    public static Graph randomDirected(int n, int m, long seed, int maxWeight) {
        long maxEdges = (long) n * (n - 1);
        if (m > maxEdges) {
            throw new IllegalArgumentException("Too many edges for a directed graph");
        }
        Random rnd = new Random(seed);
        Graph g = new Graph(n, true);
        Set<Long> used = new HashSet<>();
        while (g.getEdgeCount() < m) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            if (u == v || !used.add((long) u * n + v)) {
                continue;
            }
            g.addEdge(u, v, 1 + rnd.nextInt(maxWeight));
        }
        return g;
    }

    public static Graph randomDag(int n, int m, long seed, int maxWeight) {
        long maxEdges = (long) n * (n - 1) / 2;
        if (m > maxEdges) {
            throw new IllegalArgumentException("Too many edges for a DAG");
        }
        Random rnd = new Random(seed);
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) {
            perm[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int t = perm[i]; perm[i] = perm[j]; perm[j] = t;
        }
        Graph g = new Graph(n, true);
        Set<Long> used = new HashSet<>();
        while (g.getEdgeCount() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a == b) {
                continue;
            }
            int u = perm[Math.min(a, b)];
            int v = perm[Math.max(a, b)];
            if (!used.add((long) u * n + v)) {
                continue;
            }
            g.addEdge(u, v, 1 + rnd.nextInt(maxWeight));
        }
        return g;
    }

    private static long undirectedKey(int u, int v, int n) {
        int a = Math.min(u, v);
        int b = Math.max(u, v);
        return (long) a * n + b;
    }
}
