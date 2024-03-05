package pda.core.dependency;

import pda.common.java.D4jSubject;
import pda.core.dependency.graph.DependencyGraphVertex;
import pda.core.dependency.graph.VariableVertex;

public class DependencyMain {

    public static void main(String[] args) {
        D4jSubject subject = new D4jSubject("E:\\lvshare\\project\\", "math", 1);
        DependencyParser dependencyParser = new DependencyParser();
        dependencyParser.parse("C://Users//12588//Desktop//math_1", subject);
//        Map<String, DependencyGraphVertex> a =  dependencyParser.getDependencyGraph().getVertexes();
//        Iterator<Map.Entry<String, DependencyGraphVertex>> iterator = a.entrySet().iterator();
//        while (iterator.hasNext()){
//            Map.Entry<String, DependencyGraphVertex> entry = iterator.next();
//            if (entry.getKey().contains("org.apache.commons.math3.fraction.BigFraction.BigFraction")){
//                System.out.println(entry.getKey());
//            }
//        }
        DependencyGraphVertex vertex1 = new VariableVertex("org.apache.commons.math3.fraction.BigFraction", "BigFraction", 132, "den", 43);
        DependencyGraphVertex vertex2 = new VariableVertex("org.apache.commons.math3.fraction.BigFraction", "BigFraction", 145, "numerator", 12);
        System.out.println(dependencyParser.getDependencyTrace(vertex1, vertex2));
    }

}
