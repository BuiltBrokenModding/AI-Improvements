package com.builtbroken.ai.improvements;

import com.builtbroken.ai.improvements.modifier.ModifierSystem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Created by Dark on 7/20/2015.
 */
@Mod.EventBusSubscriber(modid = AIImprovements.DOMAIN, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(AIImprovements.DOMAIN)
public class AIImprovements
{
    public static final String DOMAIN = "aiimprovements";

    public AIImprovements()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigMain.CONFIG_SPEC);
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event)
    {
        FastTrig.init();
        ModifierSystem.init();
    }
}
