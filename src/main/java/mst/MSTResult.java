package mst;
import graph.Edge;
import java.util.List;

public class MSTResult {
    public final List<Edge> edges;
    public final long totalWeight;
    public final boolean connected;
    public final long edgeExaminations;
    public final long comparisons;
    public final long dsuOperations;

    public MSTResult(List<Edge> edges, long totalWeight, boolean connected,
                     long edgeExaminations, long comparisons, long dsuOperations){
        this.edges = edges;
        this.totalWeight = totalWeight;
        this.connected = connected;
        this.edgeExaminations = edgeExaminations;
        this.comparisons = comparisons;
        this.dsuOperations = dsuOperations;
    }

}
