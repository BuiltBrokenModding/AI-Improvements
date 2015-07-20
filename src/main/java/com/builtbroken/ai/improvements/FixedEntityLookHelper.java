package com.builtbroken.ai.improvements;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.util.MathHelper;

/**
 * Created by Dark on 7/20/2015.
 */
public class FixedEntityLookHelper extends EntityLookHelper
{
    public FixedEntityLookHelper(EntityLiving entity)
    {
        super(entity);
    }

    @Override
    public void onUpdateLook()
    {
        this.entity.rotationPitch = 0.0F;

        if (this.isLooking)
        {
            this.isLooking = false;

            double d0 = this.posX - this.entity.posX;
            double d1 = this.posY - (this.entity.posY + (double)this.entity.getEyeHeight());
            double d2 = this.posZ - this.entity.posZ;
            double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d2 * d2);

            float f = (float)(tan(d2, d0) * 180.0D / Math.PI) - 90.0F;
            float f1 = (float)(-(tan(d1, d3) * 180.0D / Math.PI));
            this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, f1, this.deltaLookPitch);
            this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, f, this.deltaLookYaw);
        }
        else
        {
            this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, 10.0F);
        }

        float f2 = MathHelper.wrapAngleTo180_float(this.entity.rotationYawHead - this.entity.renderYawOffset);

        if (!this.entity.getNavigator().noPath())
        {
            if (f2 < -75.0F)
            {
                this.entity.rotationYawHead = this.entity.renderYawOffset - 75.0F;
            }

            if (f2 > 75.0F)
            {
                this.entity.rotationYawHead = this.entity.renderYawOffset + 75.0F;
            }
        }
    }



    public static float tan(double a, double b)
    {
        return (float)Math.atan2(a, b);
    }
}
