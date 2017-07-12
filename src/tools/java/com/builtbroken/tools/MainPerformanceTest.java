package com.builtbroken.tools;

import com.builtbroken.ai.improvements.FastTrig;
import com.builtbroken.ai.improvements.FixedEntityLookHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.monster.EntityZombie;

import java.util.Random;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/12/2017.
 */
public class MainPerformanceTest
{
    public static void main(String... args)
    {
        EntityLiving living = new EntityZombie(null);

        EntityLookHelper mcHelper = new EntityLookHelper(living);
        EntityLookHelper helper = new FixedEntityLookHelper(living);
        Random random = new Random();

        FastTrig.init(); //load the cache

        for (int i = 0; i < 100000; i++)
        {
            mcHelper.isLooking = true;
            helper.isLooking = true;

            int x = random.nextInt(10) - random.nextInt(10);
            int y = random.nextInt(2) - random.nextInt(2);
            int z = random.nextInt(10) - random.nextInt(10);

            mcHelper.posX = x;
            mcHelper.posY = y;
            mcHelper.posZ = z;

            helper.posX = x;
            helper.posY = y;
            helper.posZ = z;

            System.out.println("[" + i + "]--------------------------------------");
            long start = System.nanoTime();
            mcHelper.onUpdateLook();
            start = System.nanoTime() - start;

            System.out.println("MC: " + start);

            start = System.nanoTime();
            helper.onUpdateLook();
            start = System.nanoTime() - start;

            System.out.println("BBM: " + start);
        }
    }
}
