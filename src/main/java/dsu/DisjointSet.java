package dsu;

public class DisjointSet {
    private final int[] parent;
    private final int[] size;
    private int components;

    private long findCalls;
    private long unionCalls;
    private long steps;

    public DisjointSet(int n){
        if (n < 1) {
            throw new IllegalArgumentException("DSU needs at least 1 element");
        }
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
        components = n;
    }
    public int find(int x){
        findCalls++;
        int root = x;
        while(parent[root] != root){
            root = parent[root];
            steps++;
        }
        while (parent[x] != root){
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }
    public boolean union(int a, int b){
        unionCalls++;
        int ra = find(a);
        int rb = find(b);
        if (ra == rb) {
            return false;
        }
        if (size[ra] < size[rb]) {
            int t = ra;
            ra = rb;
            rb = t;
        }
        parent[rb] = ra;
        size[ra] += size[rb];
        components--;
        return true;
    }
    public boolean connected(int a, int b){
        return find(a) == find(b);
    }
    public int getComponents(){return components;}
    public long getFindCalls(){return findCalls;}
    public long getUnionCalls(){return unionCalls;}
    public long getSteps(){return steps;}
}
