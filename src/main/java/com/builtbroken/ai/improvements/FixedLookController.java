package com.builtbroken.ai.improvements;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.util.math.MathHelper;

/**
 * Created by Dark on 7/20/2015.
 */
public class FixedLookController extends LookController
{

    public FixedLookController(MobEntity entity)
    {
        super(entity);
    }

    @Override
    protected float getXRotD()
    {
        double lvt_1_1_ = this.wantedX - this.mob.getX();
        double lvt_3_1_ = this.wantedY - (this.mob.getY() + this.mob.getEyeHeight());
        double lvt_5_1_ = this.wantedZ - this.mob.getZ();
        double lvt_7_1_ = MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_);
        return (float) (-(tan(lvt_3_1_, lvt_7_1_) * 57.2957763671875D));
    }

    @Override
    protected float getYRotD()
    {
        double lvt_1_1_ = this.wantedX - this.mob.getX();
        double lvt_3_1_ = this.wantedZ - this.mob.getZ();
        return (float) (tan(lvt_3_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
    }


    public static float tan(double a, double b)
    {
        return FastTrig.atan2(a, b);
    }

    public void copyDataIntoSelf(LookController oldHelper)
    {
        wantedX = oldHelper.getWantedX();
        wantedY = oldHelper.getWantedY();
        wantedZ = oldHelper.getWantedZ();
        hasWanted = oldHelper.isHasWanted();
        xMaxRotAngle = oldHelper.xMaxRotAngle;
        yMaxRotSpeed = oldHelper.yMaxRotSpeed;
    }
}
