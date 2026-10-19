package dsu;

public class ConfigurableDSU {
    private final int[] parent;
    private final int[] size;
    private final boolean compress;
    private final boolean bySize;
    private long steps;

    public ConfigurableDSU(int n, boolean compress, boolean bySize) {
        this.parent = new int[n];
        this.size = new int[n];
        this.compress = compress;
        this.bySize = bySize;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int x) {
        int root = x;
        while (parent[root] != root) {
            root = parent[root];
            steps++;
        }
        if (compress) {
            while (parent[x] != root) {
                int next = parent[x];
                parent[x] = root;
                x = next;
            }
        }
        return root;
    }

    public boolean union(int a, int b) {
        int ra = find(a);
        int rb = find(b);
        if (ra == rb) {
            return false;
        }
        if (bySize && size[ra] < size[rb]) {
            int t = ra; ra = rb; rb = t;
        }
        parent[rb] = ra;   // without bySize: the second root always goes under the first
        size[ra] += size[rb];
        return true;
    }

    public long getSteps() { return steps; }
}
