package com.builtbroken.ai.improvements.modifier.filters;

import net.minecraft.world.entity.Entity;

public final class NoopNode extends FilterNode
{
    public static final NoopNode INSTANCE = new NoopNode(null);

    private NoopNode(IFilterNode action)
    {
        super(action);
    }

    @Override
    public FilterResult handle(Entity entity)
    {
        return FilterResult.DID_NOTHING;
    }
}
