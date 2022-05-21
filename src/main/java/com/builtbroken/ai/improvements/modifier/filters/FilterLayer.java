package com.builtbroken.ai.improvements.modifier.filters;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.world.entity.Entity;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/2/2019.
 */
public class FilterLayer extends FilterNode
{
    private FilterNode[] nodes = new FilterNode[5];

    private int size = 0;

    public FilterLayer(IFilterNode filter)
    {
        super(filter);
    }

    public void add(IFilterNode modifier)
    {
        if (modifier == null)
        {
            modifier = NoopNode.INSTANCE;
        }

        if (size == nodes.length)
        {
            nodes = Arrays.copyOf(nodes, nodes.length + 5);
        }
        nodes[size++] = new FilterNode(modifier);
    }

    @Override
    public FilterResult handle(Entity entity)
    {
        FilterResult filterResult = action != null ? action.handle(entity) : FilterResult.FILTERED;
        if (filterResult != FilterResult.DID_NOTHING && nodes != null)
        {
            //just to be safe
            for (int i = 0; i < nodes.length; i++)
            {
                if(nodes[i] == null)
                    nodes[i] = NoopNode.INSTANCE;
            }

            for (int i = 0; i < nodes.length; i++)
            {
                final FilterNode modifier = nodes[i];

                filterResult = modifier.handle(entity);
                if (filterResult == FilterResult.FILTERED)
                {
                    //increase call count
                    modifier.callCount += 1;

                    //Resort and clear
                    if (modifier.callCount >= (Integer.MAX_VALUE / 2))
                    {
                        Arrays.sort(nodes, Comparator.comparingInt(m -> m.callCount));
                        final int sum = Arrays.stream(nodes).mapToInt(m -> m.callCount).sum();
                        Arrays.stream(nodes).forEach(m -> m.callCount = (int) Math.floor(m.callCount / (double) sum));
                    }
                    else if (i != 0 && modifier.callCount > nodes[i - 1].callCount)
                    {
                        final FilterNode m = nodes[i];

                        //Move down
                        nodes[i] = nodes[i - 1];

                        //Move up
                        nodes[i - 1] = m;
                    }
                    return filterResult;
                }
            }
        }
        return filterResult;
    }
}
