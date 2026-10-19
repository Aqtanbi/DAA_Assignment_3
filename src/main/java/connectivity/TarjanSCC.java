package connectivity;

import graph.Edge;
import graph.Graph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class TarjanSCC {
    private final Graph g;
    private final int[] disc;
    private final int[] low;
    private final boolean[] onStack;
    private final Deque<Integer> stack = new ArrayDeque<>();
    private final List<List<Integer>> components = new ArrayList<>();
    private final int[] componentId;
    private int timer = 0;
    private long examined = 0;

    private TarjanSCC(Graph g) {
        this.g = g;
        int n = g.getVertexCount();
        disc = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        componentId = new int[n];
        Arrays.fill(disc, -1);
        Arrays.fill(componentId, -1);
    }

    public static SCCResult run(Graph g) {
        if (!g.isDirected()) {
            throw new IllegalArgumentException("SCC needs a directed graph");
        }
        TarjanSCC t = new TarjanSCC(g);
        for (int v = 0; v < g.getVertexCount(); v++) {
            if (t.disc[v] == -1) {
                t.dfs(v);
            }
        }
        return new SCCResult(t.components, t.componentId, t.examined);
    }

    private void dfs(int v) {
        disc[v] = low[v] = timer++;
        stack.push(v);
        onStack[v] = true;

        for (Edge e : g.getNeighbors(v)) {
            examined++;
            int w = e.to;
            if (disc[w] == -1) {
                dfs(w);
                low[v] = Math.min(low[v], low[w]);
            } else if (onStack[w]) {
                low[v] = Math.min(low[v], disc[w]);
            }
        }

        if (low[v] == disc[v]) {             // v is the root of a component
            List<Integer> comp = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                componentId[w] = components.size();
                comp.add(w);
            } while (w != v);
            components.add(comp);
        }
    }
}