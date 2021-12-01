package com.builtbroken.ai.improvements;

import java.util.Optional;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

/**
 * Created by Dark on 7/20/2015.
 */
public class FixedLookControl extends LookControl
{
    public FixedLookControl(Mob entity)
    {
        super(entity);
    }

    @Override
    protected Optional<Float> getXRotD()
    {
        double lvt_1_1_ = this.wantedX - this.mob.getX();
        double lvt_3_1_ = this.wantedY - (this.mob.getY() + this.mob.getEyeHeight());
        double lvt_5_1_ = this.wantedZ - this.mob.getZ();
        double lvt_7_1_ = Mth.sqrt((float)(lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_));
        return Optional.of((float) (-(tan(lvt_3_1_, lvt_7_1_) * 57.2957763671875D)));
    }

    @Override
    protected Optional<Float> getYRotD()
    {
        double lvt_1_1_ = this.wantedX - this.mob.getX();
        double lvt_3_1_ = this.wantedZ - this.mob.getZ();
        return Optional.of((float) (tan(lvt_3_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F);
    }

    public static float tan(double a, double b)
    {
        return FastTrig.atan2(a, b);
    }

    public void copyDataIntoSelf(LookControl oldHelper)
    {
        wantedX = oldHelper.getWantedX();
        wantedY = oldHelper.getWantedY();
        wantedZ = oldHelper.getWantedZ();
        lookAtCooldown = oldHelper.lookAtCooldown;
        xMaxRotAngle = oldHelper.xMaxRotAngle;
        yMaxRotSpeed = oldHelper.yMaxRotSpeed;
    }
}
