package pda.core.dependency;

import org.eclipse.jdt.core.dom.*;
import pda.common.java.D4jSubject;
import pda.common.utils.*;
import pda.core.dependency.graph.*;

import java.util.*;

public class DependencyParser {

    DependencyGraph dependencyGraph;

    public void parse(String traceFile, D4jSubject subject){
        TraceParser traceParser = new TraceParser(traceFile);
        List<Pair<MethodDeclaration, String>> trace = traceParser.parse(subject);
        genGraph(trace);
    }

    private List<SimpleName> collectAllVarAtLine(MethodDeclaration node, int lineNo){
        List<SimpleName> result = new ArrayList<>();
        CompilationUnit compilationUnit = (CompilationUnit) node.getRoot();
        node.accept(new ASTVisitor() {
            @Override
            public boolean visit(SimpleName node) {
                if (compilationUnit.getLineNumber(node.getStartPosition()) == lineNo){
                    if (node.resolveBinding() instanceof IMethodBinding){
                        return true;
                    }else if(node.resolveBinding() instanceof ITypeBinding){
                        return true;
                    }else {
                        result.add(node);
                    }
                }
                return true;
            }
        });
        return result;
    }

    private List<SimpleName> collectAllVar(ASTNode node){
        List<SimpleName> result = new ArrayList<>();
        if (node == null) return result;
        node.accept(new ASTVisitor() {
            @Override
            public boolean visit(SimpleName node) {
                if (node.resolveBinding() instanceof IMethodBinding){
                    return true;
                }else if(node.resolveBinding() instanceof ITypeBinding){
                    return true;
                }else {
                    result.add(node);
                }
                return true;
            }
        });
        return result;
    }

    private void genGraph(List<Pair<MethodDeclaration, String>> trace){
        System.out.println("Starting generate graph....");
        dependencyGraph = new DependencyGraph();
        for (Pair<MethodDeclaration, String> pair: trace){
            MethodDeclaration methodDeclaration = pair.getFirst();
            String lineNo = pair.getSecond().split(":")[1];
            String clazz = pair.getSecond().split(":")[0];
            int i = 1;
            List<SimpleName> simpleNames = collectAllVarAtLine(methodDeclaration, Integer.parseInt(lineNo));
            for (Object node:methodDeclaration.parameters()){
                List<SimpleName> list = collectAllVar((ASTNode) node);
                SimpleName simpleName = list.get(list.size() - 1);
                dependencyGraph.addVertex(new MethodInvocationVertex((CompilationUnit) simpleName.getRoot(), simpleName, methodDeclaration.getName().toString(), clazz, i));
                i++;
            }
            for (SimpleName simpleName: simpleNames){
                dependencyGraph.addVertex(new VariableVertex((CompilationUnit) simpleName.getRoot(), simpleName, methodDeclaration.getName().toString(), clazz));
            }
        }

        genDefUseEdge(trace);
        genDataDependencyEdge(trace);
        genControlDependencyEdge(trace);
        genMethodInvocationEdge(trace);

        System.out.println("Finish generate graph!");
    }

    private void genDefUseEdge(List<Pair<MethodDeclaration, String>> trace){
        Map<String, DependencyGraphVertex> nameToVertex = new HashMap<>();
        Map<String, SimpleName> nameToSimpleName = new HashMap<>();
        for (Pair<MethodDeclaration, String> pair: trace){
            MethodDeclaration methodDeclaration = pair.getFirst();
            int lineNo = Integer.parseInt(pair.getSecond().split(":")[1]);
            CompilationUnit compilationUnit = (CompilationUnit) methodDeclaration.getRoot();
            String clazz = pair.getSecond().split(":")[0];
            methodDeclaration.accept(new ASTVisitor() {
                @Override
                public boolean visit(VariableDeclarationFragment node) {
                    if (compilationUnit.getLineNumber(node.getStartPosition()) != lineNo) {
                        return true;
                    }
                    List<SimpleName> rightList = collectAllVar(node.getInitializer());
                    for (SimpleName s: rightList){
                        if (nameToVertex.containsKey(s.toString())){
                            if (isInSameStatement(nameToSimpleName.get(s.toString()), s)){
                                VariableVertex endVertex = new VariableVertex((CompilationUnit) s.getRoot(), s, methodDeclaration.getName().toString(), clazz);
                                DependencyGraphVertex startVertex = nameToVertex.get(s.toString());
                                dependencyGraph.addEdge(startVertex, endVertex, EdgeType.DEF_USE);
//                                System.out.println(startVertex + "->" + endVertex);
                            }
                        }
                    }

                    List<SimpleName> leftList = collectAllVar(node.getName());
                    for (SimpleName s: leftList){
                        nameToSimpleName.put(s.toString(), s);
                        nameToVertex.put(s.toString(), new VariableVertex((CompilationUnit) s.getRoot(), s, methodDeclaration.getName().toString(), clazz));
                    }
                    return true;
                }

                @Override
                public boolean visit(Assignment node) {
                    if (compilationUnit.getLineNumber(node.getStartPosition()) != lineNo) {
                        return true;
                    }
                    List<SimpleName> rightList = collectAllVar(node.getRightHandSide());
                    for (SimpleName s: rightList){
                        if (nameToVertex.containsKey(s.toString())){
                            if (isInSameStatement(nameToSimpleName.get(s.toString()), s)){
                                VariableVertex endVertex = new VariableVertex((CompilationUnit) s.getRoot(), s, methodDeclaration.getName().toString(), clazz);
                                DependencyGraphVertex startVertex = nameToVertex.get(s.toString());
                                dependencyGraph.addEdge(startVertex, endVertex, EdgeType.DEF_USE);
//                                System.out.println(startVertex + "->" + endVertex);
                            }
                        }
                    }

                    List<SimpleName> leftList = collectAllVar(node.getLeftHandSide());
                    for (SimpleName s: leftList){
                        nameToSimpleName.put(s.toString(), s);
                        nameToVertex.put(s.toString(), new VariableVertex((CompilationUnit) s.getRoot(), s, methodDeclaration.getName().toString(), clazz));
                    }
                    return true;
                }
            });
        }
    }

    // 检测node1的定义是否能影响到node2
    private boolean isInSameStatement(SimpleName node1, SimpleName node2){
        if (node1.equals(node2)) {
            return true;
        }
        ASTNode tempNode = node1, tempNode2 = node2;
        while (tempNode.getParent() != null && !(tempNode.getParent() instanceof MethodDeclaration)){
            tempNode = tempNode.getParent();
        }
        if (tempNode.getParent() == null) {
            return false;
        }
        while (tempNode2.getParent() != null){
            if (tempNode2.getParent() instanceof MethodDeclaration){
                if (tempNode2.getParent().equals(tempNode.getParent())){
                    return true;
                }
            }
            tempNode2 = tempNode2.getParent();
        }
        return false;
    }

    private void genDataDependencyEdge(List<Pair<MethodDeclaration, String>> trace){
        for (Pair<MethodDeclaration, String> pair: trace){
            MethodDeclaration methodDeclaration = pair.getFirst();
            int lineNo = Integer.parseInt(pair.getSecond().split(":")[1]);
            CompilationUnit compilationUnit = (CompilationUnit) methodDeclaration.getRoot();
            String clazz = pair.getSecond().split(":")[0];
            methodDeclaration.accept(new ASTVisitor() {
                @Override
                public boolean visit(Assignment node) {
                    if (compilationUnit.getLineNumber(node.getStartPosition()) != lineNo) {
                        return true;
                    }
                    List<SimpleName> leftList = collectAllVar(node.getLeftHandSide());
                    List<SimpleName> rightList = collectAllVar(node.getRightHandSide());
                    SimpleName leftSimpleName = leftList.get(0);
                    VariableVertex startVertex = new VariableVertex((CompilationUnit) leftSimpleName.getRoot(), leftSimpleName, methodDeclaration.getName().toString(), clazz);
                    for (SimpleName simpleName: rightList){
                        VariableVertex endVertex = new VariableVertex((CompilationUnit) simpleName.getRoot(), simpleName, methodDeclaration.getName().toString(), clazz);
                        // 保证所有的边都是被依赖方指向依赖方
                        dependencyGraph.addEdge(endVertex, startVertex, EdgeType.DATA_DEPENDENCY);
                    }
                    return true;
                }

                @Override
                public boolean visit(VariableDeclarationFragment node) {
                    if (compilationUnit.getLineNumber(node.getStartPosition()) != lineNo) {
                        return true;
                    }
                    SimpleName leftSimpleName = node.getName();
                    List<SimpleName> rightList = collectAllVar(node.getInitializer());
                    VariableVertex startVertex = new VariableVertex((CompilationUnit) leftSimpleName.getRoot(), leftSimpleName, methodDeclaration.getName().toString(), clazz);
                    for (SimpleName simpleName: rightList){
                        VariableVertex endVertex = new VariableVertex((CompilationUnit) simpleName.getRoot(), simpleName, methodDeclaration.getName().toString(), clazz);
                        // 保证所有的边都是被依赖方指向依赖方
                        dependencyGraph.addEdge(endVertex, startVertex, EdgeType.DATA_DEPENDENCY);
                    }
                    return true;
                }
            });
        }
    }

    private void genControlDependencyEdge(List<Pair<MethodDeclaration, String>> trace){
        for (int i = 0;i < trace.size();i++){
            Pair<MethodDeclaration, String> pair = trace.get(i);
            MethodDeclaration methodDeclaration = pair.getFirst();
            CompilationUnit compilationUnit = (CompilationUnit) methodDeclaration.getRoot();
            int lineNo = Integer.parseInt(pair.getSecond().split(":")[1]);
            String clazz = pair.getSecond().split(":")[0];
            int i1 = i;
            methodDeclaration.accept(new ASTVisitor() {
                @Override
                public boolean visit(IfStatement node) {
                    if (compilationUnit.getLineNumber(node.getExpression().getStartPosition()) != lineNo) {
                        return true;
                    }
                    List<SimpleName> simpleNames = findVarInIfStatement(node, trace.subList(i1 + 1, trace.size()));
                    List<SimpleName> conditionSimpleNames = collectAllVar(node.getExpression());
                    for (SimpleName startSimpleName:conditionSimpleNames){
                        VariableVertex vertex = new VariableVertex((CompilationUnit) startSimpleName.getRoot(), startSimpleName, methodDeclaration.getName().toString(), clazz);
                        for (SimpleName endSimpleName:simpleNames){
                            VariableVertex endVertex = new VariableVertex((CompilationUnit) endSimpleName.getRoot(), endSimpleName, methodDeclaration.getName().toString(), clazz);
                            if (getDependencyGraph().getVertexes().containsKey(vertex.getVertexId()) && getDependencyGraph().getVertexes().containsKey(endVertex.getVertexId())){
                                dependencyGraph.addEdge(vertex, endVertex, EdgeType.CONTROL_DEPENDENCY);
                            }
                        }
                    }
                    return true;
                }
            });
        }
    }

    private List<SimpleName> findVarInIfStatement(IfStatement ifStatement, List<Pair<MethodDeclaration, String>> subTrace){
        List<SimpleName> simpleNames = new ArrayList<>();
        for (Pair<MethodDeclaration, String> pair: subTrace) {
            MethodDeclaration methodDeclaration = pair.getFirst();
            CompilationUnit compilationUnit = (CompilationUnit) methodDeclaration.getRoot();
            int lineNo = Integer.parseInt(pair.getSecond().split(":")[1]);
            String clazz = pair.getSecond().split(":")[0];
            methodDeclaration.accept(new ASTVisitor() {
                @Override
                public boolean visit(SimpleName node) {
                    if (compilationUnit.getLineNumber(node.getStartPosition()) != lineNo){
                        return true;
                    }
                    if(isInSubTree(node, ifStatement) && !isInSubTree(node, ifStatement.getExpression())){
                        simpleNames.add(node);
                    }
                    return true;
                }
            });
        }
        return simpleNames;
    }

    private boolean isInSubTree(ASTNode child, ASTNode parent){
        ASTNode tempNode = child;
        while(tempNode.getParent() != null && !tempNode.getParent().equals(parent) ){
            tempNode = tempNode.getParent();
        }
        return tempNode.getParent() != null;
    }

    private void genMethodInvocationEdge(List<Pair<MethodDeclaration, String>> trace){
        for (int i = 0;i < trace.size();i++){
            Pair<MethodDeclaration, String> pair = trace.get(i);
            MethodDeclaration methodDeclaration = pair.getFirst();
            int lineNo = Integer.parseInt(pair.getSecond().split(":")[1]);
            CompilationUnit compilationUnit = (CompilationUnit) methodDeclaration.getRoot();
            String clazz = pair.getSecond().split(":")[0];
            int i1 = i;
            methodDeclaration.accept(new ASTVisitor() {
                @Override
                public boolean visit(MethodInvocation node) {
                    if (compilationUnit.getLineNumber(node.getStartPosition()) != lineNo) {
                        return true;
                    }
                    List<List<SimpleName>> methodInvocationArgs = new ArrayList<>();
                    for (Object astNode: node.arguments()){
                        List<SimpleName> list = collectAllVar((ASTNode) astNode);
                        methodInvocationArgs.add(list);
                    }
                    List<String> methodDelArgs = findAllArgumentForMethod(trace.subList(i1 +1, trace.size()), node.getName().toString(), methodInvocationArgs.size());
                    for (int i = 0;i < methodInvocationArgs.size();i++){
                        if (methodInvocationArgs.size() != methodDelArgs.size()){
//                            System.out.println(pair.getSecond() + ":" + node.getName().toString() + ":" + methodDelArgs + "    " +  methodInvocationArgs);
                            break;
                        }
                        List<SimpleName> list = methodInvocationArgs.get(i);
                        String methodDelArg = methodDelArgs.get(i);
                        MethodInvocationVertex methodInvocationVertex = (MethodInvocationVertex) dependencyGraph.getVertexes().get(methodDelArg);
                        if (methodInvocationVertex == null){
                            continue;
                        }
                        for (SimpleName simpleName:list){
                            VariableVertex vertex = new VariableVertex((CompilationUnit) simpleName.getRoot(), simpleName, methodDeclaration.getName().toString(), clazz);
                            dependencyGraph.addEdge(vertex, methodInvocationVertex, EdgeType.METHOD_INVOCATION);
                        }
                    }
                    return true;
                }
            });
        }
    }

    private List<String> findAllArgumentForMethod(List<Pair<MethodDeclaration, String>> subTrace, String methodName, int argNum){
        List<ASTNode> args = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (Pair<MethodDeclaration, String> pair: subTrace){
            MethodDeclaration methodDeclaration = pair.getFirst();
            String clazz = pair.getSecond().split(":")[0];
            if (methodDeclaration.getName().toString().contains(methodName)){
                if (checkMethodNum(methodDeclaration, argNum)){
                    int size = methodDeclaration.parameters().size();
                    // 最后一位可能有不定参数
                    if (argNum <= size){
                        args.addAll(methodDeclaration.parameters().subList(0, argNum));
                    }else {
                        args.addAll(methodDeclaration.parameters());
                        for (int i = 0;i < argNum - size;i++){
                            args.add((ASTNode) methodDeclaration.parameters().get(size - 1));
                        }
                    }
                    for (int i = 0;i < args.size();i++){
                        SingleVariableDeclaration singleVariableDeclaration = (SingleVariableDeclaration)args.get(i);
                        int lineNo = ((CompilationUnit) singleVariableDeclaration.getRoot()).getLineNumber(singleVariableDeclaration.getStartPosition());
                        result.add("MethodArgument#" + (i + 1) + "#" + clazz + "." + methodDeclaration.getName() + ":" + lineNo + "-" + singleVariableDeclaration.getName().toString());
                    }
                    break;
                }

            }
        }
        return result;
    }

    private boolean checkMethodNum(MethodDeclaration m, int argNum){
        List args = m.parameters();
        if (args.size() == argNum){
            return true;
        }
        // 检查最后一个参数是否为不定参数
        if (args.size() == 0) {
            return false;
        }

        if (((SingleVariableDeclaration) args.get(args.size() - 1)).toString().contains("...")){
            return true;
        }
        return false;
    }

    public DependencyGraph getDependencyGraph(){
        return dependencyGraph;
    }

    public boolean hasDependency(String clazz, String methodName, int lineNo, String varName, int colNo, String clazz2, String methodName2, int lineNo2, String varName2, int colNo2){
        VariableVertex vertex1 = new VariableVertex(clazz, methodName, lineNo, varName, colNo);
        VariableVertex vertex2 = new VariableVertex(clazz2, methodName2, lineNo2, varName2, colNo);
        return dependencyGraph.hasDependency(vertex1, vertex2);
    }

    public List<String> getDependencyTrace(DependencyGraphVertex v1, DependencyGraphVertex v2){
        return dependencyGraph.getDependencyTrace(v1, v2);
    }

}
