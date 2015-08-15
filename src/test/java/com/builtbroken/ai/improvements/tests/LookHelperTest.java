package com.builtbroken.ai.improvements.tests;

import com.builtbroken.ai.improvements.AIImprovements;
import com.builtbroken.ai.improvements.FastTrig;
import com.builtbroken.ai.improvements.FixedEntityLookHelper;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.util.Random;

/**
 * Created by Dark on 7/20/2015.
 */
@RunWith(VoltzTestRunner.class)
public class LookHelperTest extends AbstractTest
{
    public static AIImprovements mod = new AIImprovements();

    @Override
    public void setUpForEntireClass()
    {
        FastTrig.init();
    }

    //TODO test math
    //TODO test ram usage
    //TODO test precision
    public void testTanMethod()
    {
        double a = 10;
        double b = 10;

        float tan = (float) Math.atan2(a, b);
        float tan2 = FixedEntityLookHelper.tan(a, b);
        Assert.assertTrue("Tan return doesn't match, Math.atan2 returned " + tan +"  FastTrig.atan2 returned " + tan2,tan == tan2);
    }

    public void testTiming()
    {
        int runs = 1000000;
        long sumTan = 0;
        long sumTan2 = 0;
        Random rand = new Random();
        for (int i = 0; i < runs; i++)
        {
            double a = rand.nextFloat() * 10 - rand.nextFloat() * 10;
            double b = rand.nextFloat() * 10 - rand.nextFloat() * 10;
            //System.out.println("Math.atan2()  " + sumTan);
            long time = System.nanoTime();
            float tan = (float) Math.atan2(a, b);
            time = System.nanoTime() - time;
            sumTan += time;
            //System.out.println("    Delta  " + time);
        }
        for (int i = 0; i < runs; i++)
        {
            double a = rand.nextFloat() * 10 - rand.nextFloat() * 10;
            double b = rand.nextFloat() * 10 - rand.nextFloat() * 10;
            //System.out.println("FastTrig.atan2()  " + sumTan2);
            long time = System.nanoTime();
            float tan = FastTrig.atan2(a, b);
            time = System.nanoTime() - time;
            sumTan2 += time;
            //System.out.println("    Delta  " + time);
        }

        long avTan = sumTan / runs;
        long avTan2 = sumTan2 / runs;

        System.out.println("Math.atan2(a, b): " + avTan + "  FastTrig.atan2(a, b):" + avTan2);
        Assert.assertTrue(avTan2 < avTan);
    }
}
