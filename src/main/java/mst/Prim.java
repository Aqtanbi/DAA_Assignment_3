package mst;

import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {

    public static MSTResult run(Graph g){
        if (g.isDirected()) {
            throw new IllegalArgumentException("MST needs an undirected graph");
        }
        int n = g.getVertexCount();
        boolean[] visited = new boolean[n];

        long[] comparisons = {0};
        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> {
            comparisons[0]++;
            return Integer.compare(a.weight, b.weight);
        });

        List<Edge> mst = new ArrayList<>();
        long total = 0;
        long examined = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }
            visited[start] = true;
            examined += scan(g, start, visited, pq);

            while (!pq.isEmpty()){
                Edge e = pq.poll();
                if (visited[e.to]){
                    continue;
                }
                visited[e.to] = true;
                mst.add(e);
                total += e.weight;
                if (mst.size() == n - 1) {
                    break;
                }
                examined += scan(g, e.to, visited, pq);
            }
            pq.clear();
        }
        boolean connected = (mst.size() == n - 1);
        return new MSTResult(mst, total, connected, examined, comparisons[0], 0);
    }
    private static long scan(Graph g, int v, boolean[] visited, PriorityQueue<Edge> pq){
        long count = 0;
        for (Edge e : g.getNeighbors(v)) {
            count++;
            if (!visited[e.to]) {
                pq.add(e);
            }
        }
        return count;
    }

}
