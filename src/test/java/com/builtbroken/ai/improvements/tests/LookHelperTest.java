package com.builtbroken.ai.improvements.tests;

import com.builtbroken.ai.improvements.AIImprovements;
import com.builtbroken.ai.improvements.FastTrig;
import com.builtbroken.ai.improvements.FixedEntityLookHelper;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import org.junit.Assert;
import org.junit.runner.RunWith;

/**
 * Created by Dark on 7/20/2015.
 */
@RunWith(VoltzTestRunner.class)
public class LookHelperTest extends AbstractTest
{
    public static AIImprovements mod = new AIImprovements();

    public void testTanMethod()
    {
        double a = 10;
        double b = 10;
        float tan = (float) Math.atan2(a, b);
        float tan2 = FixedEntityLookHelper.tan(a, b);
        Assert.assertTrue(tan == tan2);
    }

    public void testTiming()
    {
        long sumTan = 0;
        long sumTan2 = 0;
        double a = 10;
        double b = 10;
        for (int i = 0; i < 1000; i++)
        {
            Long start = System.nanoTime();
            float tan = (float) Math.atan2(a, b);
            Long end = System.nanoTime();
            sumTan += end - start;
        }
        FastTrig.init();
        for (int i = 0; i < 1000; i++)
        {
            Long start = System.nanoTime();
            float tan2 = FastTrig.atan2(a, b);
            Long end = System.nanoTime();
            sumTan2 += end - start;
        }

        long avTan = sumTan / 1000;
        long avTan2 = sumTan2 / 1000;

        System.out.println("avTan: " + avTan + "  avTan2:" + avTan2);
        Assert.assertTrue(avTan2 < avTan);
    }
}
