package benchmark;

import connectivity.SCCResult;
import connectivity.TarjanSCC;
import connectivity.TopoResult;
import connectivity.TopologicalSort;
import dsu.ConfigurableDSU;
import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;
import shortestpath.DijkstraResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    static final long SEED = 42;
    static final int MAX_WEIGHT = 100;
    static final int WARMUP_RUNS = 30;
    static final long WARMUP_NANOS = 300_000_000;        // 0.3 s
    static final int REPEATS = 5;
    static final long TARGET_REPEAT_NANOS = 20_000_000;  // each repeat lasts at least ~20 ms

    static final String HEADER =
            "graph,V,E,algorithm,avg_time_us,edge_examinations,comparisons,dsu_ops,result";

    interface Task {
        Object run();
    }

    static Object sink;

    static double averageMicros(Task t) {

        long warmStart = System.nanoTime();
        int warm = 0;
        while (warm < WARMUP_RUNS || System.nanoTime() - warmStart < WARMUP_NANOS) {
            sink = t.run();
            warm++;
        }
        long probeStart = System.nanoTime();
        sink = t.run();
        long probe = Math.max(1, System.nanoTime() - probeStart);
        int inner = (int) Math.min(10000, Math.max(1, TARGET_REPEAT_NANOS / probe));

        double sumPerRun = 0;
        for (int r = 0; r < REPEATS; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < inner; i++) {
                sink = t.run();
            }
            sumPerRun += (System.nanoTime() - start) / (double) inner;
        }
        return sumPerRun / REPEATS / 1000.0;
    }

    static String row(String label, int n, int m, String algo, double us,
                      long examined, long comparisons, long dsuOps, String result) {
        return String.format(Locale.US, "%s,%d,%d,%s,%.2f,%d,%d,%d,%s",
                label, n, m, algo, us, examined, comparisons, dsuOps, result);
    }

    static List<String> runConfig(String label, int n, int m) {
        List<String> rows = new ArrayList<>();
        Graph und = GraphGenerator.connectedUndirected(n, m, SEED, MAX_WEIGHT);
        Graph dir = GraphGenerator.randomDirected(n, m, SEED + 1, MAX_WEIGHT);
        Graph dag = GraphGenerator.randomDag(n, m, SEED + 2, MAX_WEIGHT);

        // Kruskal
        double t = averageMicros(() -> Kruskal.run(und));
        MSTResult k = Kruskal.run(und);
        rows.add(row(label, n, m, "Kruskal", t, k.edgeExaminations, k.comparisons,
                k.dsuOperations, "weight=" + k.totalWeight + " connected=" + k.connected));

        // Prim
        t = averageMicros(() -> Prim.run(und));
        MSTResult p = Prim.run(und);
        rows.add(row(label, n, m, "Prim", t, p.edgeExaminations, p.comparisons, 0,
                "weight=" + p.totalWeight + " connected=" + p.connected));

        // SCC
        t = averageMicros(() -> TarjanSCC.run(dir));
        SCCResult s = TarjanSCC.run(dir);
        int biggest = 0;
        for (List<Integer> c : s.components) {
            biggest = Math.max(biggest, c.size());
        }
        rows.add(row(label, n, m, "SCC", t, s.edgeExaminations, 0, 0,
                "components=" + s.count + " largest=" + biggest));

        // Topological order on a DAG
        t = averageMicros(() -> TopologicalSort.run(dag));
        TopoResult td = TopologicalSort.run(dag);
        rows.add(row(label, n, m, "Topo-DAG", t, td.edgeExaminations, 0, 0,
                "isDag=" + td.isDag + " valid="
                        + TopologicalSort.isValidOrder(dag, td.order)));

        // Topological order on a graph with cycles
        t = averageMicros(() -> TopologicalSort.run(dir));
        TopoResult tc = TopologicalSort.run(dir);
        rows.add(row(label, n, m, "Topo-cyclic", t, tc.edgeExaminations, 0, 0,
                "isDag=" + tc.isDag + " ordered=" + tc.order.size() + "/" + n));

        // Dijkstra from vertex 0 on the directed graph
        t = averageMicros(() -> Dijkstra.run(dir, 0));
        DijkstraResult d = Dijkstra.run(dir, 0);
        rows.add(row(label, n, m, "Dijkstra", t, d.edgeExaminations, d.comparisons, 0,
                "reachable=" + d.reachableCount() + " relaxations=" + d.relaxations
                        + " heapOps=" + d.heapOperations));
        return rows;
    }

    static List<String> runDsuExperiment() {
        List<String> rows = new ArrayList<>();
        rows.add("scenario,n,variant,steps,avg_time_us");
        int[] sizes = {100, 1000, 10000};
        boolean[][] variants = {{false, false}, {true, false}, {false, true}, {true, true}};
        String[] names = {"none", "compression only", "union by size only", "both"};

        for (int n : sizes) {
            for (int v = 0; v < variants.length; v++) {
                final boolean comp = variants[v][0];
                final boolean size = variants[v][1];
                final int nn = n;

                // Scenario 1 (worst case for the naive DSU): unions (1,0),(2,1),(3,2),...
                // build one long chain; then find on every element.
                long[] steps = {0};
                double us = averageMicros(() -> {
                    ConfigurableDSU ds = new ConfigurableDSU(nn, comp, size);
                    for (int i = 0; i < nn - 1; i++) {
                        ds.union(i + 1, i);
                    }
                    for (int i = 0; i < nn; i++) {
                        ds.find(i);
                    }
                    steps[0] = ds.getSteps();
                    return ds;
                });
                rows.add(String.format(Locale.US, "chain,%d,%s,%d,%.2f",
                        n, names[v], steps[0], us));

                // Scenario 2: 5n random unions, then find on every element.
                us = averageMicros(() -> {
                    ConfigurableDSU ds = new ConfigurableDSU(nn, comp, size);
                    Random rnd = new Random(SEED);
                    for (int i = 0; i < 5 * nn; i++) {
                        ds.union(rnd.nextInt(nn), rnd.nextInt(nn));
                    }
                    for (int i = 0; i < nn; i++) {
                        ds.find(i);
                    }
                    steps[0] = ds.getSteps();
                    return ds;
                });
                rows.add(String.format(Locale.US, "random,%d,%s,%d,%.2f",
                        n, names[v], steps[0], us));
            }
        }
        return rows;
    }

    static void write(Path file, List<String> lines) throws IOException {
        Files.write(file, lines, StandardCharsets.UTF_8);
        System.out.println("wrote " + file);
    }

    static List<String> toMarkdown(List<String> csv) {
        List<String> out = new ArrayList<>();
        String[] head = csv.get(0).split(",");
        StringBuilder h = new StringBuilder("|");
        StringBuilder sep = new StringBuilder("|");
        for (String c : head) {
            h.append(' ').append(c).append(" |");
            sep.append("---|");
        }
        out.add(h.toString());
        out.add(sep.toString());
        for (int i = 1; i < csv.size(); i++) {
            String[] cells = csv.get(i).split(",", head.length);
            StringBuilder r = new StringBuilder("|");
            for (String c : cells) {
                r.append(' ').append(c).append(" |");
            }
            out.add(r.toString());
        }
        return out;
    }

    static void runAll(String dir) throws IOException {
        Path tables = Paths.get(dir, "tables");
        Files.createDirectories(tables);
        Files.createDirectories(Paths.get(dir, "plots"));

        // 1. Required sizes
        List<String> required = new ArrayList<>();
        required.add(HEADER);
        required.addAll(runConfig("Small", 10, 30));
        required.addAll(runConfig("Medium", 100, 500));
        required.addAll(runConfig("Large", 1000, 5000));
        write(tables.resolve("required_sizes.csv"), required);
        write(tables.resolve("required_sizes.md"), toMarkdown(required));

        // 2. Scaling in V (E = 5V)
        List<String> scaleV = new ArrayList<>();
        scaleV.add(HEADER);
        for (int n : new int[]{100, 250, 500, 1000, 2000, 5000, 10000}) {
            scaleV.addAll(runConfig("scaleV", n, 5 * n));
        }
        write(tables.resolve("scaling_vertices.csv"), scaleV);

        // 3. Scaling in E (V = 1000)
        List<String> scaleE = new ArrayList<>();
        scaleE.add(HEADER);
        for (int m : new int[]{2000, 5000, 10000, 20000, 50000, 100000}) {
            scaleE.addAll(runConfig("scaleE", 1000, m));
        }
        write(tables.resolve("scaling_edges.csv"), scaleE);

        // 4. DSU optimizations
        List<String> dsu = runDsuExperiment();
        write(tables.resolve("dsu_experiment.csv"), dsu);
        write(tables.resolve("dsu_experiment.md"), toMarkdown(dsu));
    }

    public static void main(String[] args) throws Exception {
        final String dir = args.length > 0 ? args[0] : "results";
        // Large stack: Tarjan's DFS is recursive and goes deep on big graphs.
        final Throwable[] error = {null};
        Thread worker = new Thread(null, () -> {
            try {
                runAll(dir);
            } catch (Throwable e) {
                error[0] = e;
            }
        }, "benchmark", 512L * 1024 * 1024);
        worker.start();
        worker.join();
        if (error[0] != null) {
            error[0].printStackTrace();
            System.exit(1);
        }
    }
}
