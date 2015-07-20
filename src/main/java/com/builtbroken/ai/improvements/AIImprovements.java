package com.builtbroken.ai.improvements;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLModDisabledEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Iterator;

/**
 * Created by Dark on 7/20/2015.
 */
@Mod(modid = "aiimprovements", name = "AI Improvements", version = AIImprovements.VERSION, acceptableRemoteVersions = "*", canBeDeactivated = true)
public class AIImprovements
{

    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

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
        REMOVE_LOOK_AI = config.getBoolean("RemoveEntityAILookIdle", Configuration.CATEGORY_GENERAL, REMOVE_LOOK_IDLE, "Disabled the AI segment that controls entities looking at random locations");
        REMOVE_LOOK_AI = config.getBoolean("ReplaceLookHelper", Configuration.CATEGORY_GENERAL, REPLACE_LOOK_HELPER, "Replaces the EntityLookHelper with a more CPU efficient version");
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
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        //TODO
        Entity entity = event.entity;
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
