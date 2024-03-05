/**
 * Copyright (C) CIC, TJU, PRC. - All Rights Reserved.
 * Unauthorized copying of this file via any medium is
 * strictly prohibited Proprietary and Confidential.
 * Written by Jiajun Jiang<jiangjiajun@tju.edu.cn>.
 */

package pda.core.dependency;

import org.junit.Test;
import pda.TestCase;
import pda.common.java.D4jSubject;
import pda.common.utils.Utils;

/**
 * @author: Jiajun
 * @date: 2021/12/14
 */
public class DependencyMainTest extends TestCase {

    @Test
    public void test() {
        D4jSubject subject = new D4jSubject(testResBase, "math", 1);
        DependencyParser dependencyParser = new DependencyParser();
        dependencyParser.parse(Utils.join(testResBase, "math", "math_1_buggy"), subject);
    }

}
