package pda.core.dependency.graph;

public class MethodInvocationEdge extends DependencyGraphEdge{
    public MethodInvocationEdge(DependencyGraphVertex start, DependencyGraphVertex end) {
        super(start, end, EdgeType.METHOD_INVOCATION);
    }
}
