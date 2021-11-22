package com.builtbroken.ai.improvements;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class AnimalConfigSection
{
	private final BooleanValue removeFloat;
	private final BooleanValue removePanic;
	private final BooleanValue removeBreed;
	private final BooleanValue removeTempt;
	private final BooleanValue removeFollowParent;
	private final BooleanValue removeStroll;

	public AnimalConfigSection(BooleanValue removeFloat, BooleanValue removePanic, BooleanValue removeBreed, BooleanValue removeTempt, BooleanValue removeFollowParent, BooleanValue removeStroll)
	{
		this.removeFloat = removeFloat;
		this.removePanic = removePanic;
		this.removeBreed = removeBreed;
		this.removeTempt = removeTempt;
		this.removeFollowParent = removeFollowParent;
		this.removeStroll = removeStroll;
	}

	public BooleanValue removeFloat()
	{
		return removeFloat;
	}

	public BooleanValue removePanic()
	{
		return removePanic;
	}

	public BooleanValue removeBreed()
	{
		return removeBreed;
	}

	public BooleanValue removeTempt()
	{
		return removeTempt;
	}

	public BooleanValue removeFollowParent()
	{
		return removeFollowParent;
	}

	public BooleanValue removeStroll()
	{
		return removeStroll;
	}
}