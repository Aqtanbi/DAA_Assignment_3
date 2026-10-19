package connectivity;

import graph.Edge;
import graph.Graph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TopologicalSort {
    public static TopoResult run(Graph g){
        if (!g.isDirected()){
            throw new IllegalArgumentException("Topological order needs a directed graph");
        }
        int n = g.getVertexCount();
        long examined = 0;

        int[] indegree = new int[n];
        for (Edge e: g.getEdges()){
            indegree[e.to]++;
            examined++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int v = 0; v < n; v++){
            if (indegree[v] == 0) {
                queue.add(v);
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()){
            int v = queue.poll();
            order.add(v);
            for (Edge e : g.getNeighbors(v)){
                examined++;
                indegree[e.to]--;
                if (indegree[e.to] == 0) {
                    queue.add(e.to);
                }
            }
        }

        boolean isDag = (order.size() == n);
        return new TopoResult(order, isDag, examined);
    }

    public static boolean isValidOrder(Graph g, List<Integer> order){
        int n = g.getVertexCount();
        if (order.size() != n) {
            return false;
        }
        int[] position = new int[n];
        boolean[] seen = new boolean[n];
        for (int i = 0; i < n; i++) {
            int v = order.get(i);
            if (seen[v]){
                return false; //repeated vertex
            }
            seen[v] = true;
            position[v] = i;
        }
        for (Edge e : g.getEdges()){
            if (position[e.from] >= position[e.to]) {
                return false;
            }
        }
        return true;
    }
}
