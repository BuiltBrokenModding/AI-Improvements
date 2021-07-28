package com.builtbroken.ai.improvements.modifier.editor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/2/2019.
 */
public class ModifierLayer
{
    private ModifierNode[] nodes = new ModifierNode[5];

    private int size = 0;
    private boolean resortNormalize = false;

    public final boolean combatAI;

    public ModifierLayer(boolean combatAI)
    {
        this.combatAI = combatAI;
    }

    public void add(IEntityAiModifier modifier)
    {
        if (modifier != null)
        {
            if (size == nodes.length)
            {
                nodes = Arrays.copyOf(nodes, nodes.length + 5);
            }
            nodes[size++] = new ModifierNode(modifier);
        }
    }

    public void handle(Mob entity)
    {
        final GoalSelector goalSelector = combatAI ? entity.targetSelector : entity.goalSelector;

        final Set<Goal> goalsToRemove = new HashSet<>();
        for (WrappedGoal prioritizedGoal : goalSelector.availableGoals)
        {
            final Goal reGoal = process(entity, prioritizedGoal.getGoal());

            //Queue removal
            if (reGoal == null)
            {
                goalsToRemove.add(prioritizedGoal.getGoal());
            }
            //TODO handle modified goals
        }

        //Do remove
        for (Goal goal : goalsToRemove)
        {
            goalSelector.removeGoal(goal);
        }

        //Sorta data and normal
        if (resortNormalize)
        {
            resortNormalize = false;
            resort(true);
        }
    }

    protected Goal process(Mob entity, Goal goal)
    {
        for (int i = 0; i < nodes.length; i++)
        {
            final ModifierNode modifier = nodes[i];

            if (modifier != null)
            {
                final Goal reGoal = modifier.handle(entity, goal);

                //Trigger sorting
                if (reGoal != goal && i != 0 && modifier.callCount > nodes[i - 1].callCount)
                {
                    bubble(modifier, i);
                }

                //Return if we changed the goal
                if (reGoal != goal)
                {
                    return reGoal;
                }
            }
        }
        return goal;
    }

    protected void resort(boolean normalize)
    {
        Arrays.sort(nodes, Comparator.comparingInt(m -> m.callCount));
        if (normalize)
        {
            final int sum = Arrays.stream(nodes).mapToInt(m -> m.callCount).sum();
            Arrays.stream(nodes).forEach(m -> m.callCount = (int) Math.floor(m.callCount / (double) sum));
        }
    }

    protected void bubble(ModifierNode modifier, int i)
    {
        //increase call count
        modifier.callCount += 1;

        //Resort and clear
        if (modifier.callCount >= (Integer.MAX_VALUE / 2))
        {
            resortNormalize = true;
        }
        else
        {
            final ModifierNode m = nodes[i];

            //Move down
            nodes[i] = nodes[i - 1];

            //Move up
            nodes[i - 1] = m;
        }
    }
}
