package pda.core.slice;

import pda.common.java.D4jSubject;
import pda.core.dependency.DependencyParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Slicer {
    DependencyParser dependencyParser;

    public Slicer(D4jSubject subject){
        dependencyParser = new DependencyParser();
        dependencyParser.parse("C://Users//12588//Desktop//math_1", subject);
    }

    public List<String> slice(String criterion){
        List<String> result = new ArrayList<>();
        return result;
    }
}
