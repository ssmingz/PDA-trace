package pda.core.dependency.graph;

public class DefUseEdge extends DependencyGraphEdge{

    public DefUseEdge(DependencyGraphVertex start, DependencyGraphVertex end) {
        super(start, end, EdgeType.DEF_USE);
    }

}
