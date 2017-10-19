package com.builtbroken.ai.improvements.overrides;

import com.builtbroken.ai.improvements.AIImprovements;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.io.File;
import java.util.HashMap;

/**
 * Handles applying changes to entities
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/19/2017.
 */
public class OverrideHandler
{
    public static final OverrideHandler INSTANCE = new OverrideHandler();

    public static HashMap<String, EntityOverride> globalList = new HashMap();
    //TODO implement per entity list to improve performance

    public static void registerOverride(EntityOverride override)
    {
        if (globalList.containsKey(override.id))
        {
            AIImprovements.LOGGER.warn("OverrideHandler: replacing existing override [id=" + override.id + " value=" + globalList.get(override.id) + "] with " + override);
        }
        globalList.put(override.id, override);
    }

    public void init(Configuration configuration)
    {
        //Handle configs
        for (EntityOverride override : globalList.values())
        {
            if (override != null)
            {
                override.enabled = configuration.getBoolean(override.id, "enable_override_handlers", override.enabled, override.description);
            }
        }

        //Handle settings
        for (EntityOverride override : globalList.values())
        {
            if(override != null && override.enabled)
            {
                override.loadSettings(new Configuration(new File(AIImprovements.configFolder, "overrides/" + override.id + ".cfg")));
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        for (EntityOverride override : globalList.values())
        {
            if (override != null && override.enabled)
            {
                override.applyChanges(event.entity);
            }
        }
    }
}
