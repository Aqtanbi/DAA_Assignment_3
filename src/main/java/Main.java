import graph.Graph;
import dsu.DisjointSet;

public class Main {
    public static void main(String[] args) {
        DisjointSet ds = new DisjointSet(5);
        System.out.println(ds.union(0, 1));
        System.out.println(ds.union(2, 3));
        System.out.println(ds.union(1, 0));
        System.out.println(ds.union(1, 3));
        System.out.println(ds.getComponents());
        System.out.println(ds.connected(0, 3));
        System.out.println(ds.connected(4, 0));
    }
}
