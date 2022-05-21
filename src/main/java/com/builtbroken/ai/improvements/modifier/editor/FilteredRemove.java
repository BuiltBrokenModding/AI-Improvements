package com.builtbroken.ai.improvements.modifier.editor;

import com.builtbroken.ai.improvements.ConfigMain;
import com.builtbroken.ai.improvements.FilteredConfigValue;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

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
	public Goal handle(MobEntity entity, Goal aiTask)
	{
		if (ConfigMain.CONFIG.allowRemoveCalls.get() && instanceCheck.isGoal(aiTask))
		{
			//if it's an allowlist, mobs on the filter list should be handled
			//if it's not an allowlist (denylist), the mobs NOT on the filter should be handled
			if (filteredConfigValue.isFiltered(entity.getType().getRegistryName().toString()) && filteredConfigValue.configValue().get())
			{
				return null;
			}
		}

		return aiTask;
	}
}
