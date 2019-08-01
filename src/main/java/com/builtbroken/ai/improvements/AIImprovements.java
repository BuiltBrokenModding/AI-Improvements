package com.builtbroken.ai.improvements;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Dark on 7/20/2015.
 */
@Mod.EventBusSubscriber
@Mod("aiimprovements")
public class AIImprovements
{
    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event)
    {
        FastTrig.init();
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        //TODO add improved and configurable mob spawners
        //TODO add ability to block placing mob spawners or break them
        //TODO recode AI look classes to only run when near a player since they are only visual effects
        //TODO maybe also code to only run client side? that is if there is no effect?
        //TODO add config options for Fast math helper
        final Entity entity = event.getEntity();
        if (entity instanceof MobEntity)
        {
            final MobEntity living = (MobEntity) entity;
            if (ConfigMain.REMOVE_LOOK_AI || ConfigMain.REMOVE_LOOK_IDLE)
            {
                final Set<PrioritizedGoal> goals = living.goalSelector.goals;
                final Iterator<PrioritizedGoal> it = goals.iterator();
                while (it.hasNext())
                {
                    final Goal goal = it.next().getGoal();
                    if (goal instanceof LookAtGoal)
                    {
                        if (ConfigMain.REMOVE_LOOK_AI)
                        {
                            it.remove();
                        }
                    }
                    else if (goal instanceof LookRandomlyGoal)
                    {
                        if (ConfigMain.REMOVE_LOOK_IDLE)
                        {
                            it.remove();
                        }
                    }
                }
            }

            //Only replace vanilla look helper to avoid overlapping mods
            if (ConfigMain.REPLACE_LOOK_HELPER && (living.getLookController() == null || living.getLookController().getClass() == LookController.class))
            {
                //Get old so we can copy data
                final LookController oldHelper = living.getLookController();

                //Set new
                living.lookController = new FixedLookController(living);

                //Instance of check may look unneeded but some mods do stupid things
                if (living.getLookController() instanceof FixedLookController)
                {
                    ((FixedLookController) living.getLookController()).copyDataIntoSelf(oldHelper);
                }
                else
                {
                    //TODO error/warning in console, then mark this entity as unusable for future checks
                }

            }
        }
    }
}
