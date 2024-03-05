package pda.core.dependency.graph;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;

public class VariableVertex extends DependencyGraphVertex{
    private String clazz;
    private SimpleName simpleName;
    private String methodName;
    private String varName;
    private int lineNo;
    private int colNo;

    public VariableVertex(String clazz, String methodName, int lineNo, String varName, int colNo){
        this.clazz = clazz;
        this.methodName = methodName;
        this.lineNo = lineNo;
        this.varName = varName;
        this.vertexType = VertexType.Variable;
        this.colNo = colNo;
    }

    public VariableVertex(CompilationUnit compilationUnit, SimpleName simpleName, String methodName, String clazz){
        this.simpleName = simpleName;
        this.lineNo = compilationUnit.getLineNumber(simpleName.getStartPosition());
        this.colNo = compilationUnit.getColumnNumber(simpleName.getStartPosition());
        this.clazz = clazz;
        this.methodName = methodName;
        this.vertexType = VertexType.Variable;
    }

    public String getClazz() {
        return clazz;
    }

    public SimpleName getSimpleName() {
        return simpleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String toString(){
        return vertexType.toString() + "#" + clazz + "." + methodName + ":" + lineNo + "|" + colNo + "-" + ((simpleName == null)?varName:simpleName.toString());
    }

    public String getVertexId(){
        return this.toString();
    }

}
