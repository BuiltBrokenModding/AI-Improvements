package com.builtbroken.ai.improvements.tests;

import com.builtbroken.ai.improvements.AIImprovements;
import com.builtbroken.ai.improvements.FixedEntityLookHelper;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.junit.Assert;
import org.junit.runner.RunWith;

/**
 * Created by Dark on 7/20/2015.
 */
@RunWith(VoltzTestRunner.class)
public class ReplacementTest extends AbstractTest
{
    public static AIImprovements mod = new AIImprovements();

    public void testZombieInit()
    {
        //Test that the entity inits with the look helper
        EntityZombie zombie = new EntityZombie(null);
        boolean foundAIWatchClosest = false;
        boolean foundAILookIdle = false;
        for (Object obj : zombie.tasks.taskEntries)
        {
            if (obj instanceof EntityAITasks.EntityAITaskEntry)
            {
                EntityAITasks.EntityAITaskEntry task = (EntityAITasks.EntityAITaskEntry) obj;
                if (task.action instanceof EntityAIWatchClosest)
                {
                    foundAIWatchClosest = true;
                }
                else if (task.action instanceof EntityAILookIdle)
                {
                    foundAILookIdle = true;
                }
            }
        }
        Assert.assertTrue("Zombie seems to be missing EntityAIWatchClosest", foundAIWatchClosest);
        Assert.assertTrue("Zombie seems to be missing EntityAILookIdle", foundAILookIdle);

        Assert.assertTrue("Zombie didn't init with a look helper", zombie.getLookHelper() != null);
        Assert.assertTrue("Zombie didn't init with a vanilla look helper", zombie.getLookHelper().getClass() == EntityLookHelper.class);
    }

    public void testRemoveLookAtAI()
    {
        AIImprovements.REMOVE_LOOK_AI = true;

        EntityZombie zombie = new EntityZombie(null);
        boolean foundAIWatchClosest = false;

        EntityJoinWorldEvent event = new EntityJoinWorldEvent(zombie, null);
        mod.onEntityJoinWorld(event);
        for (Object obj : zombie.tasks.taskEntries)
        {
            if (obj instanceof EntityAITasks.EntityAITaskEntry)
            {
                EntityAITasks.EntityAITaskEntry task = (EntityAITasks.EntityAITaskEntry) obj;
                if (task.action instanceof EntityAIWatchClosest)
                {
                    foundAIWatchClosest = true;
                }
            }
        }
        Assert.assertTrue("Event failed to remove EntityAIWatchClosest AI object", !foundAIWatchClosest);
        AIImprovements.REMOVE_LOOK_AI = false;
    }

    public void testDoNotRemoveLookAtAI()
    {
        AIImprovements.REMOVE_LOOK_AI = false;

        EntityZombie zombie = new EntityZombie(null);
        boolean foundAIWatchClosest = false;

        EntityJoinWorldEvent event = new EntityJoinWorldEvent(zombie, null);
        mod.onEntityJoinWorld(event);
        for (Object obj : zombie.tasks.taskEntries)
        {
            if (obj instanceof EntityAITasks.EntityAITaskEntry)
            {
                EntityAITasks.EntityAITaskEntry task = (EntityAITasks.EntityAITaskEntry) obj;
                if (task.action instanceof EntityAIWatchClosest)
                {
                    foundAIWatchClosest = true;
                }
            }
        }
        Assert.assertTrue("Event removed EntityAIWatchClosest AI object when it shouldn't have", foundAIWatchClosest);
    }

    public void testRemoveLookIdleAI()
    {
        AIImprovements.REMOVE_LOOK_IDLE = true;

        EntityZombie zombie = new EntityZombie(null);
        boolean foundAILookIdle = false;

        EntityJoinWorldEvent event = new EntityJoinWorldEvent(zombie, null);
        mod.onEntityJoinWorld(event);
        for (Object obj : zombie.tasks.taskEntries)
        {
            if (obj instanceof EntityAITasks.EntityAITaskEntry)
            {
                EntityAITasks.EntityAITaskEntry task = (EntityAITasks.EntityAITaskEntry) obj;
                if (task.action instanceof EntityAILookIdle)
                {
                    foundAILookIdle = true;
                }
            }
        }
        Assert.assertTrue("Event failed to remove EntityAILookIdle AI object", !foundAILookIdle);
        AIImprovements.REMOVE_LOOK_AI = false;
    }

    public void testDoNotRemoveLookIdleAI()
    {
        AIImprovements.REMOVE_LOOK_IDLE = false;

        EntityZombie zombie = new EntityZombie(null);
        boolean foundAILookIdle = false;

        EntityJoinWorldEvent event = new EntityJoinWorldEvent(zombie, null);
        mod.onEntityJoinWorld(event);
        for (Object obj : zombie.tasks.taskEntries)
        {
            if (obj instanceof EntityAITasks.EntityAITaskEntry)
            {
                EntityAITasks.EntityAITaskEntry task = (EntityAITasks.EntityAITaskEntry) obj;
                if (task.action instanceof EntityAILookIdle)
                {
                    foundAILookIdle = true;
                }
            }
        }
        Assert.assertTrue("Event removed EntityAILookIdle AI object when it shouldn't have", foundAILookIdle);
    }

    public void testReplaceLookHelper()
    {
        AIImprovements.REPLACE_LOOK_HELPER = true;
        EntityZombie zombie = new EntityZombie(null);

        EntityJoinWorldEvent event = new EntityJoinWorldEvent(zombie, null);
        mod.onEntityJoinWorld(event);

        Assert.assertTrue("Zombie's look helper seems to have been set null", zombie.getLookHelper() != null);
        Assert.assertTrue("Zombie's look helper was not changed to our look helper", zombie.getLookHelper().getClass() == FixedEntityLookHelper.class);
    }
}
