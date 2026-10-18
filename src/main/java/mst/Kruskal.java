package mst;

import dsu.DisjointSet;
import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.List;

public class Kruskal {
    public static MSTResult run(Graph g){
        if (g.isDirected()){
            throw new IllegalArgumentException("MST needs an undirected graph");
        }
        int n = g.getVertexCount();
        List<Edge> sorted = new ArrayList<>(g.getEdges());
        long[] comparisons = {0};
        sorted.sort((a, b) ->{
            comparisons[0]++;
            return Integer.compare(a.weight, b.weight);
        });

        DisjointSet ds = new DisjointSet(n);
        List<Edge> mst = new ArrayList<>();
        long total = 0;
        long examined = 0;

        for (Edge e : sorted) {
            if (mst.size() == n - 1) {
                break;
            }
            examined++;
            if (ds.union(e.from, e.to)) {
                mst.add(e);
                total += e.weight;
            }
        }
        boolean connected = (mst.size() == n - 1);
        long dsuOps = ds.getFindCalls() + ds.getUnionCalls();
        return new MSTResult(mst, total, connected, examined, comparisons[0], dsuOps);
    }
}
