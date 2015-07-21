package com.builtbroken.ai.improvements.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;

/**
 * Created by Dark on 7/21/2015.
 */
public class FixedEntityLookAtClosest extends EntityAIWatchClosest
{
    public FixedEntityLookAtClosest(EntityLiving entity, Class cz, float distance)
    {
        super(entity, cz, distance);
    }

    public FixedEntityLookAtClosest(EntityLiving entity, Class cz, float distance, float updateRndTrigger)
    {
        super(entity, cz, distance, updateRndTrigger);
    }
}
