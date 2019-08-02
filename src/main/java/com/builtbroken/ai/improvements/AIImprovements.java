package com.builtbroken.ai.improvements;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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
    public AIImprovements()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigMain.CONFIG_SPEC);
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event)
    {
        FastTrig.init();
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        final boolean allowRemoves = ConfigMain.CONFIG.allowRemoveCalls.get();
        //TODO add improved and configurable mob spawners
        //TODO add ability to block placing mob spawners or break them
        //TODO recode AI look classes to only run when near a player since they are only visual effects
        //TODO maybe also code to only run client side? that is if there is no effect?
        //TODO add config options for Fast math helper
        final Entity entity = event.getEntity();
        if (entity instanceof AbstractFishEntity)
        {
            if(allowRemoves)
            {
                final AbstractFishEntity fish = (AbstractFishEntity) entity;

                final Set<PrioritizedGoal> goals = fish.goalSelector.goals;
                final Iterator<PrioritizedGoal> it = goals.iterator();
                while (it.hasNext())
                {
                    final Goal goal = it.next().getGoal();
                    if (goal instanceof RandomSwimmingGoal)
                        //TODO build out as lambda system to save time/code
                        //TODO use a filter tree (goal remover loop -> mobs -> (fish -> remove calls, zombie -> remove calls))
                    {
                        if (ConfigMain.CONFIG.removeFishSwim.get())
                        {
                            it.remove();
                        }
                    }
                    else if (goal instanceof AvoidEntityGoal)
                    {
                        if (ConfigMain.CONFIG.removeFishAvoidPlayer.get())
                        {
                            it.remove();
                        }
                    }
                    else if (goal instanceof PanicGoal)
                    {
                        if (ConfigMain.CONFIG.removeFishPanic.get())
                        {
                            it.remove();
                        }
                    }
                }
            }
        }
        else if (entity instanceof MobEntity)
        {
            final MobEntity living = (MobEntity) entity;
            if (allowRemoves && (ConfigMain.CONFIG.removeLookGoal.get() || ConfigMain.CONFIG.removeLookRandom.get()))
            {
                final Set<PrioritizedGoal> goals = living.goalSelector.goals;
                final Iterator<PrioritizedGoal> it = goals.iterator();
                while (it.hasNext())
                {
                    final Goal goal = it.next().getGoal();
                    if (goal instanceof LookAtGoal)
                    {
                        if (ConfigMain.CONFIG.removeLookGoal.get())
                        {
                            it.remove();
                        }
                    }
                    else if (goal instanceof LookRandomlyGoal)
                    {
                        if (ConfigMain.CONFIG.removeLookRandom.get())
                        {
                            it.remove();
                        }
                    }
                }
            }

            //Only replace vanilla look helper to avoid overlapping mods
            if (ConfigMain.CONFIG.replaceLookController.get() && (living.getLookController() == null || living.getLookController().getClass() == LookController.class))
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

            //TODO squid
            //this.goalSelector.addGoal(0, new SquidEntity.MoveRandomGoal(this));
            //        this.goalSelector.addGoal(1, new SquidEntity.FleeGoal());
        }
    }
}
