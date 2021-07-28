package com.builtbroken.ai.improvements.modifier.filters;

import net.minecraft.world.entity.Entity;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/2/2019.
 */
@FunctionalInterface
public interface IFilterNode
{
    /**
     * Called to consume an entity
     *
     * @param entity - entity being modified
     * @return true to consume, false to move on
     */
    FilterResult handle(Entity entity);
}
