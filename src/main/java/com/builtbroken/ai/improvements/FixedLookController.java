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
	protected float getTargetPitch()
	{
		double lvt_1_1_ = this.posX - this.mob.getPosX();
		double lvt_3_1_ = this.posY - (this.mob.getPosY() + this.mob.getEyeHeight());
		double lvt_5_1_ = this.posZ - this.mob.getPosZ();
		double lvt_7_1_ = MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_);
		return (float) (-(tan(lvt_3_1_, lvt_7_1_) * 57.2957763671875D));
	}

	@Override
	protected float getTargetYaw()
	{
		double lvt_1_1_ = this.posX - this.mob.getPosX();
		double lvt_3_1_ = this.posZ - this.mob.getPosZ();
		return (float) (tan(lvt_3_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
	}


	public static float tan(double a, double b)
	{
		return FastTrig.atan2(a, b);
	}

	public void copyDataIntoSelf(LookController oldHelper)
	{
		posX = oldHelper.getLookPosX();
		posY = oldHelper.getLookPosY();
		posZ = oldHelper.getLookPosZ();
		isLooking = oldHelper.getIsLooking();
		deltaLookPitch = oldHelper.deltaLookPitch;
		deltaLookYaw = oldHelper.deltaLookYaw;
	}
}
