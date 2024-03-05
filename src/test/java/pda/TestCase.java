/**
 * Copyright (C) CIC, TJU, PRC. - All Rights Reserved.
 * Unauthorized copying of this file via any medium is
 * strictly prohibited Proprietary and Confidential.
 * Written by Jiajun Jiang<jiangjiajun@tju.edu.cn>.
 */

package pda;

import pda.common.conf.Constant;
import pda.common.utils.Utils;

/**
 * @author: Jiajun
 * @date: 2021/11/19
 */
public abstract class TestCase {

    protected final String testResBase = Utils.join(Constant.RES_DIR, "forTest");
}
