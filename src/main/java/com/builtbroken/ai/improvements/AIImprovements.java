package com.builtbroken.ai.improvements;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Iterator;

/**
 * Created by Dark on 7/20/2015.
 */
@Mod(modid = "aiimprovements", name = "AI Improvements", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@", acceptableRemoteVersions = "*", canBeDeactivated = true)
public class AIImprovements
{
    public static Logger LOGGER;

    public static boolean REMOVE_LOOK_AI = false;
    public static boolean REMOVE_LOOK_IDLE = false;
    public static boolean REPLACE_LOOK_HELPER = true;

    @Mod.EventHandler
    public void disableEvent(FMLModDisabledEvent event)
    {
        LOGGER.info("Disabling mod");
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/AI_Improvements.cfg"));
        config.load();
        REMOVE_LOOK_AI = config.getBoolean("RemoveEntityAIWatchClosest", Configuration.CATEGORY_GENERAL, REMOVE_LOOK_AI, "Disabled the AI segment that controls entities looking at the closest player");
        REMOVE_LOOK_IDLE = config.getBoolean("RemoveEntityAILookIdle", Configuration.CATEGORY_GENERAL, REMOVE_LOOK_IDLE, "Disabled the AI segment that controls entities looking at random locations");
        REPLACE_LOOK_HELPER = config.getBoolean("ReplaceLookHelper", Configuration.CATEGORY_GENERAL, REPLACE_LOOK_HELPER, "Replaces the EntityLookHelper with a more CPU efficient version");
        config.save();
        LOGGER = LogManager.getLogger("AI_Improvements");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        FastTrig.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        //TODO add improved and configurable mob spawners
        //TODO add ability to block placing mob spawners or break them
        //TODO recode AI look classes to only run when near a player since they are only visual effects
        //TODO maybe also code to only run client side? that is if there is no effect?
        //TODO add config options for Fast math helper
        Entity entity = event.getEntity();
        if (entity instanceof EntityLiving)
        {
            EntityLiving living = (EntityLiving) entity;
            if (REMOVE_LOOK_AI || REMOVE_LOOK_IDLE)
            {
                Iterator it = living.tasks.taskEntries.iterator();
                while (it.hasNext())
                {
                    Object obj = it.next();
                    if (obj instanceof EntityAITasks.EntityAITaskEntry)
                    {
                        EntityAITasks.EntityAITaskEntry task = (EntityAITasks.EntityAITaskEntry) obj;
                        if (REMOVE_LOOK_AI && task.action instanceof EntityAIWatchClosest)
                        {
                            it.remove();
                        }
                        else if (REMOVE_LOOK_IDLE && task.action instanceof EntityAILookIdle)
                        {
                            it.remove();
                        }
                    }
                }
            }

            //Only replace vanilla look helper to avoid overlapping mods
            if (REPLACE_LOOK_HELPER && (living.getLookHelper() == null || living.getLookHelper().getClass() == EntityLookHelper.class))
            {
                EntityLookHelper oldHelper = living.lookHelper;
                living.lookHelper = new FixedEntityLookHelper(living);

                //Not sure if needed but updating just in case
                living.lookHelper.posX = oldHelper.posX;
                living.lookHelper.posX = oldHelper.posX;
                living.lookHelper.posX = oldHelper.posX;
                living.lookHelper.isLooking = oldHelper.isLooking;
                living.lookHelper.deltaLookPitch = oldHelper.deltaLookPitch;
                living.lookHelper.deltaLookYaw = oldHelper.deltaLookYaw;
            }
        }
    }
}
