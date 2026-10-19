package shortestpath;

import graph.Edge;
import graph.Graph;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Dijkstra {

    public static DijkstraResult run(Graph g, int source) {
        int n = g.getVertexCount();
        if (source < 0 || source >= n) {
            throw new IllegalArgumentException("Source out of range: " + source);
        }
        for (Edge e : g.getEdges()) {
            if (e.weight < 0) {
                throw new IllegalArgumentException(
                        "Dijkstra requires non-negative edge weights, found: " + e);
            }
        }

        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, DijkstraResult.INF);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        long[] comparisons = {0};
        // queue entries: {distance, vertex}
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> {
            comparisons[0]++;
            return Long.compare(a[0], b[0]);
        });
        pq.add(new long[]{0, source});

        long examined = 0;
        long relaxations = 0;
        long heapOps = 1;                       // the initial add

        while (!pq.isEmpty()) {
            long[] top = pq.poll();
            heapOps++;
            int v = (int) top[1];
            if (top[0] > dist[v]) {
                continue;                       // outdated entry, skip
            }
            for (Edge e : g.getNeighbors(v)) {
                examined++;
                long nd = dist[v] + e.weight;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    parent[e.to] = v;
                    relaxations++;
                    pq.add(new long[]{nd, e.to});
                    heapOps++;
                }
            }
        }
        return new DijkstraResult(source, dist, parent, examined, relaxations, heapOps,
                comparisons[0]);
    }
}
