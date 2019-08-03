package com.builtbroken.ai.improvements.modifier.editor;

import net.minecraft.entity.ai.goal.Goal;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/3/2019.
 */
@FunctionalInterface
public interface InstanceCheck
{
    boolean isGoal(Goal goal);
}
