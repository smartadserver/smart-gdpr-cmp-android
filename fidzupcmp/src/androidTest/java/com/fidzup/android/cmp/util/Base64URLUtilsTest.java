package com.fidzup.android.cmp.util;

import junit.framework.Assert;

import org.junit.Test;


public class Base64URLUtilsTest {

    @Test
    public void testEncodeString() {
        Assert.assertEquals("dGVzdCBzdHJpbmc", Base64URLUtils.getBase64URL("test string"));
    }

    @Test
    public void testDecodeString() {
        String decoded = Base64URLUtils.decodeString("dGVzdCBzdHJpbmc", false);
        Assert.assertEquals("test string", decoded);
    }

    @Test
    public void testEncodeData() {
        Assert.assertEquals("dGVzdCBzdHJpbmc", Base64URLUtils.getBase64URL("test string".getBytes()));
    }
}
