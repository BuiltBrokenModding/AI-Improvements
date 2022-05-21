package com.builtbroken.ai.improvements;

import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class FilteredConfigValue
{
	private final BooleanValue configValue;
	private final BooleanValue isAllowlist;
	private final ConfigValue<List<? extends String>> filterList;

	public FilteredConfigValue(BooleanValue configValue, BooleanValue isAllowlist, ConfigValue<List<? extends String>> filterList)
	{
		this.configValue = configValue;
		this.isAllowlist = isAllowlist;
		this.filterList = filterList;
	}

	public BooleanValue configValue()
	{
		return configValue;
	}

	public BooleanValue isAllowlist()
	{
		return isAllowlist;
	}

	public ConfigValue<List<? extends String>> filterList()
	{
		return filterList;
	}

	public boolean isFiltered(String string)
	{
		return isAllowlist.get() != filterList.get().contains(string);
	}
}
