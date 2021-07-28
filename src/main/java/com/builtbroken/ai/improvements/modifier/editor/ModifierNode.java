package com.builtbroken.ai.improvements.modifier.editor;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

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
    public Goal handle(Mob entity, Goal aiTask)
    {
        return action.handle(entity, aiTask);
    }
}
