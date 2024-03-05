/**
 * Copyright (C) CIC, TJU, PRC. - All Rights Reserved.
 * Unauthorized copying of this file via any medium is
 * strictly prohibited Proprietary and Confidential.
 * Written by Jiajun Jiang<jiangjiajun@tju.edu.cn>.
 */

package pda;


import pda.common.utils.LevelLogger;
import pda.core.trace.TraceMain;

/**
 * @author: Jiajun
 * @date: 2021/11/2
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please given the arguments");
            System.err.println("\ttrace : perform program tracing.");
            System.err.println("\tdependency : build dependency graph.");
            System.err.println("\tslice : perform program slicing.");
            System.exit(1);
        }

        switch (args[0]) {
            case "trace":
                TraceMain.main(args);
                break;
            case "dependency":
                // TODO
                break;
            case "slice":
                // TODO
                break;
            default:
                LevelLogger.error("No such command : " + args[0]);
        }

    }

}
