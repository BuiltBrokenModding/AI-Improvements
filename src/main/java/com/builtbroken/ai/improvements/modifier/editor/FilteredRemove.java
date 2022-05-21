package com.builtbroken.ai.improvements.modifier.editor;

import com.builtbroken.ai.improvements.ConfigMain;
import com.builtbroken.ai.improvements.ConfigMain.FilteredConfigValue;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class FilteredRemove implements IEntityAiModifier
{
    private final InstanceCheck instanceCheck;
    private final FilteredConfigValue filteredConfigValue;

    public FilteredRemove(InstanceCheck instanceCheck, FilteredConfigValue filteredConfigValue)
    {
        this.instanceCheck = instanceCheck;
        this.filteredConfigValue = filteredConfigValue;
    }

    @Override
    public Goal handle(Mob entity, Goal aiTask)
    {
        if (ConfigMain.CONFIG.allowRemoveCalls.get() && instanceCheck.isGoal(aiTask))
        {
            boolean isAllowlist = filteredConfigValue.isAllowlist().get();
            String registryName = entity.getType().getRegistryName().toString();

            //if it's an allowlist, mobs on the filter list should be handled
            //if it's not an allowlist (denylist), the mobs NOT on the filter should be handled
            if (isAllowlist == filteredConfigValue.filterList().get().contains(registryName) && filteredConfigValue.configValue().get())
            {
                return null;
            }
        }

        return aiTask;
    }
}
