package com.builtbroken.ai.improvements.modifier.editor;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/3/2019.
 */
public class ConfigDrivenEdit implements IEntityAiModifier
{
    private final BooleanValue configCheck;
    private final IEntityAiModifier modifier;

    public ConfigDrivenEdit(BooleanValue configCheck, IEntityAiModifier modifier)
    {
        this.configCheck = configCheck;
        this.modifier = modifier;
    }

    @Override
    public Goal handle(Mob entity, Goal aiTask)
    {
        return configCheck.get() ? modifier.handle(entity, aiTask) : aiTask;
    }

}
