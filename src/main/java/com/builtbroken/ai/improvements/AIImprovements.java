package com.builtbroken.ai.improvements;

import com.builtbroken.ai.improvements.overrides.OverrideHandler;
import com.builtbroken.ai.improvements.overrides.instances.EntityOverrideAttackOnCollide;
import com.builtbroken.ai.improvements.util.CheckFakeWorld;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

    public static File configFolder;
    public static CheckFakeWorld fakeWorld;

    Configuration config;

    @Mod.EventHandler
    public void disableEvent(FMLModDisabledEvent event)
    {
        LOGGER.info("Disabling mod");
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("AI_Improvements");
        fakeWorld = CheckFakeWorld.newWorld("fakeWorld");

        configFolder = new File(event.getModConfigurationDirectory(), "bbm/AI_Improvements");

        //Move old config, if new doesn't exist
        File oldConfig = new File(event.getModConfigurationDirectory(), "AI_Improvements.cfg");
        File newConfig = new File(configFolder, "AI_Improvements.cfg");
        if (oldConfig.exists() && !newConfig.exists())
        {
            LOGGER.info("Moving old config file " + oldConfig + " to " + newConfig);
            try
            {
                Files.move(oldConfig.toPath(), newConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);
                oldConfig.delete();
            }
            catch (IOException e)
            {
                LOGGER.error("Failed to move config");
                e.printStackTrace();
            }
        }

        //Load settings
        config = new Configuration(newConfig);
        config.load();

        REMOVE_LOOK_AI = config.getBoolean("RemoveEntityAIWatchClosest", Configuration.CATEGORY_GENERAL, REMOVE_LOOK_AI, "Disabled the AI segment that controls entities looking at the closest player");
        REMOVE_LOOK_IDLE = config.getBoolean("RemoveEntityAILookIdle", Configuration.CATEGORY_GENERAL, REMOVE_LOOK_IDLE, "Disabled the AI segment that controls entities looking at random locations");
        REPLACE_LOOK_HELPER = config.getBoolean("ReplaceLookHelper", Configuration.CATEGORY_GENERAL, REPLACE_LOOK_HELPER, "Replaces the EntityLookHelper with a more CPU efficient version");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        OverrideHandler.registerOverride(new EntityOverrideAttackOnCollide());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        FastTrig.init();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(OverrideHandler.INSTANCE);

        OverrideHandler.INSTANCE.init(config);
        config.save();
    }

    @SubscribeEvent //TODO move to override object
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        //TODO add improved and configurable mob spawners
        //TODO add ability to block placing mob spawners or break them
        //TODO recode AI look classes to only run when near a player since they are only visual effects
        //TODO maybe also code to only run client side? that is if there is no effect?
        //TODO add config options for Fast math helper
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
