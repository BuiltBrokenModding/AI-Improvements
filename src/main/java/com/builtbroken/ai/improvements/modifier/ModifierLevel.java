package com.builtbroken.ai.improvements.modifier;

import java.util.function.Function;

import com.builtbroken.ai.improvements.modifier.editor.ModifierLayer;
import com.builtbroken.ai.improvements.modifier.filters.FilterLayer;
import com.builtbroken.ai.improvements.modifier.filters.FilterNode;
import com.builtbroken.ai.improvements.modifier.filters.FilterResult;
import com.builtbroken.ai.improvements.modifier.filters.IFilterNode;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/3/2019.
 */
public class ModifierLevel extends FilterNode
{
    public final ModifierLayer goalEditor = new ModifierLayer(false);
    public final ModifierLayer combatGoalEditor = new ModifierLayer(true);
    public final FilterLayer filters = new FilterLayer(null);

    public static ModifierLevel newFilter(Function<Entity, Boolean> filter)
    {
        return new ModifierLevel(((entity) -> filter.apply(entity) ? FilterResult.FILTERED : FilterResult.DID_NOTHING));
    }

    private ModifierLevel(IFilterNode filter)
    {
        super(filter);
    }

    @Override
    public FilterResult handle(Entity entity)
    {
        FilterResult filterResult = action.handle(entity);
        if (filterResult != FilterResult.DID_NOTHING)
        {
            if (entity instanceof Mob mob)
            {
                goalEditor.handle(mob);
                combatGoalEditor.handle(mob);
            }
            return filters.handle(entity);
        }
        return filterResult;
    }
}
