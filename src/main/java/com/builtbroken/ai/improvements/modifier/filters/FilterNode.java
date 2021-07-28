package com.builtbroken.ai.improvements.modifier.filters;

import net.minecraft.world.entity.Entity;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/2/2019.
 */
public class FilterNode implements IFilterNode
{
    protected int callCount = 0;

    protected final IFilterNode action;

    protected FilterNode(IFilterNode action)
    {
        this.action = action;
    }

    @Override
    public FilterResult handle(Entity entity)
    {
        return action.handle(entity);
    }
}
