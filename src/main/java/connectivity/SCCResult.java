package connectivity;

import java.util.List;

public class SCCResult {
    public final List<List<Integer>> components;
    public final int[] componentId;
    public final int count;
    public final long edgeExaminations;

    public SCCResult(List<List<Integer>> components, int[] componentId, long edgeExaminations){
        this.components = components;
        this.componentId = componentId;
        this.count = components.size();
        this.edgeExaminations = edgeExaminations;
    }
}
