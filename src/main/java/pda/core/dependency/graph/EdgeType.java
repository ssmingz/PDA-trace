package pda.core.dependency.graph;

public enum EdgeType {
    DEF_USE,
    DATA_DEPENDENCY,
    CONTROL_DEPENDENCY,
    METHOD_INVOCATION,
    UNKNOWN
}
