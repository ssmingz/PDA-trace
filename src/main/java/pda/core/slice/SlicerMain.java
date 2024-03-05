package pda.core.slice;

import pda.common.java.D4jSubject;

public class SlicerMain {

    public static void main(String[] args) {
        D4jSubject subject = new D4jSubject("E:\\lvshare\\project\\", "math", 1);
        Slicer slicer = new Slicer(subject);
        slicer.slice("");
    }
}
