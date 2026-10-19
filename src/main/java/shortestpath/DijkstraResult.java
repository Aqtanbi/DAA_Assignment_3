package shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DijkstraResult {
    public static final long INF = Long.MAX_VALUE;

    public final int source;
    public final long[] dist;
    public final int[] parent;
    public final long edgeExaminations;
    public final long relaxations;
    public final long heapOperations;
    public final long comparisons;

    public DijkstraResult(int source, long[] dist, int[] parent, long edgeExaminations,
                          long relaxations, long heapOperations, long comparisons) {
        this.source = source;
        this.dist = dist;
        this.parent = parent;
        this.edgeExaminations = edgeExaminations;
        this.relaxations = relaxations;
        this.heapOperations = heapOperations;
        this.comparisons = comparisons;
    }

    public boolean isReachable(int v) {
        return dist[v] != INF;
    }

    public int reachableCount() {
        int count = 0;
        for (long d : dist) {
            if (d != INF) {
                count++;
            }
        }
        return count;
    }

    public List<Integer> getPath(int v) {
        List<Integer> path = new ArrayList<>();
        if (!isReachable(v)) {
            return path;
        }
        for (int cur = v; cur != -1; cur = parent[cur]) {
            path.add(cur);
        }
        Collections.reverse(path);
        return path;
    }
}
