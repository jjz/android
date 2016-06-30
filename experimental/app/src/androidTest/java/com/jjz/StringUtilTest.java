package com.jjz;

import android.test.AndroidTestCase;

import junit.framework.Assert;

public class StringUtilTest extends AndroidTestCase {


    public void testCompare() {
        Assert.assertFalse(StringUtil.compare(null,"123"));
        Assert.assertTrue(StringUtil.compare("123","123"));
    }
}
