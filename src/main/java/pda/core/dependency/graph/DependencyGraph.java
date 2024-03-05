package pda.core.dependency.graph;

import java.util.*;

public class DependencyGraph {

    Map<String, DependencyGraphVertex> vertexes;

    public DependencyGraph(){
        vertexes = new HashMap<>();
    }

    public void addVertex(DependencyGraphVertex node){
        vertexes.put(node.getVertexId(), node);
    }

    public void addEdge(DependencyGraphVertex startNode, DependencyGraphVertex endNode, EdgeType edgeType){
        if (startNode == null || endNode == null){
            System.out.println("Error edge");
            return;
        }
        if (!vertexes.containsKey(startNode.getVertexId())){
//            vertexes.put(startNode.getVertexId(), startNode);
            return;
        }
        if (!vertexes.containsKey(endNode.getVertexId())){
//            vertexes.put(endNode.getVertexId(), endNode);
            return;
        }
        vertexes.get(startNode.getVertexId()).connect(vertexes.get(endNode.getVertexId()), edgeType);
    }

    public boolean hasDependency(DependencyGraphVertex vertex1, DependencyGraphVertex vertex2){
        return hasPath(vertex1, vertex2) || hasPath(vertex2, vertex1);
    }

    private boolean hasPath(DependencyGraphVertex startVertex, DependencyGraphVertex endVertex){
        if (startVertex.sameVertex(endVertex)) return true;
        Stack<DependencyGraphVertex> stack = new Stack<>();
        stack.add(startVertex);
        while (!stack.empty()){
            DependencyGraphVertex currentVertex = stack.pop();
            for (DependencyGraphVertex v:currentVertex.toVertexes){
                if (v.sameVertex(endVertex)){
                    return true;
                }else {
                    stack.push(v);
                }
            }
        }
        return false;
    }

    public Map<String, DependencyGraphVertex> getVertexes(){
        return vertexes;
    }

    public List<String> getDependencyTrace(DependencyGraphVertex vertex1, DependencyGraphVertex vertex2){
        if (vertexes.containsKey(vertex1.getVertexId()) && vertexes.containsKey(vertex2.getVertexId())){
            vertex1 = vertexes.get(vertex1.getVertexId());
            vertex2 = vertexes.get(vertex2.getVertexId());
        }else if (!vertexes.containsKey(vertex1.getVertexId())){
            System.out.println(vertex1.getVertexId() + " is not in graph!!!");
            String line = vertex1.getVertexId().split("\\|")[0];
            String name = vertex1.getVertexId().split("-")[1];
            List<String> nearVertex = new ArrayList<>();
            for (String s : vertexes.keySet()) {
                if (s.startsWith(line) && s.endsWith(name)){
                    nearVertex.add(s);
                }
            }
            System.out.println("Perhaps it is :" + nearVertex);
            return null;
        }else {
            System.out.println(vertex2.getVertexId() + " is not in graph!!!");
            String line = vertex2.getVertexId().split("\\|")[0];
            String name = vertex2.getVertexId().split("-")[1];
            List<String> nearVertex = new ArrayList<>();
            for (String s : vertexes.keySet()) {
                if (s.startsWith(line) && s.endsWith(name)){
                    nearVertex.add(s);
                }
            }
            System.out.println("Perhaps it is :" + nearVertex);
            return null;
        }
        List<DependencyGraphVertex> result = new ArrayList<>();
        dfs(vertex1, vertex2, result, new ArrayList<>());
        List<String> edges = new ArrayList<>();
        for (int i = 1;i < result.size();i++){
            edges.add(result.get(i - 1).findStartEdge(result.get(i)).toString());
        }
        return edges;
    }

    public void dfs(DependencyGraphVertex vertex1, DependencyGraphVertex vertex2, List<DependencyGraphVertex> result, List<DependencyGraphVertex> trace){
        if (vertex1.sameVertex(vertex2)){
            result.addAll(trace);
        }else {
            // 有环直接返回
            if (trace.contains(vertex1)) return;
            for (DependencyGraphVertex v:vertex1.toVertexes){
                trace.add(vertex1);
                dfs(v,vertex2,result,trace);
                trace.remove(vertex1);
            }
        }
    }

}
