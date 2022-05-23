package com.test.app;

import com.sample.app.util.UtilityFile;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationStarterTest {

    @Test
    public void testGetCompleteFileSavePath() {
        UtilityFile fileUtil = new UtilityFile("D:/download/");
        String url = "/";
        String res = fileUtil.getCompleteFileSavePath(url);
        Assert.assertFalse(res.isEmpty());
        Assert.assertSame(true, res.endsWith("index.html"));
    }

}
