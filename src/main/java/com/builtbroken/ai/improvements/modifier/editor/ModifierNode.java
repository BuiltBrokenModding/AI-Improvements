package com.builtbroken.ai.improvements.modifier.editor;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/3/2019.
 */
public class ModifierNode implements IEntityAiModifier
{
    protected int callCount = 0;
    private final IEntityAiModifier action;

    public ModifierNode(IEntityAiModifier action)
    {
        this.action = action;
    }

    @Override
    public Goal handle(MobEntity entity, Goal aiTask)
    {
        return action.handle(entity, aiTask);
    }
}
