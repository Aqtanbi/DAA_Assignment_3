package connectivity;

import java.util.List;

public class TopoResult {
    public final List<Integer> order;
    public final boolean isDag;
    public final long edgeExaminations;

    public TopoResult(List<Integer> order, boolean isDag, long edgeExaminations){
        this.order = order;
        this.isDag =isDag;
        this.edgeExaminations = edgeExaminations;
    }
}
