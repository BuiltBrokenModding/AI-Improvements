package com.builtbroken.ai.improvements.modifier.editor;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/2/2019.
 */
@FunctionalInterface
public interface IEntityAiModifier
{
    /**
     * Called to modify an entity's AI task
     *
     * @param entity - entity being modified
     * @return null to remove, same to skip, new to replace
     */
    Goal handle(MobEntity entity, Goal aiTask);
}
