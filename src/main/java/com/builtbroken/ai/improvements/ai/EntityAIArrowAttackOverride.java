package com.builtbroken.ai.improvements.ai;

import com.builtbroken.ai.improvements.overrides.instances.EntityOverrideArrowAttack;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;

/**
 * Rewrite of {@link net.minecraft.entity.ai.EntityAIArrowAttack} to allow
 * adjustement of attack times and ranges for {@link net.minecraft.entity.monster.EntitySkeleton}
 */
public class EntityAIArrowAttackOverride extends EntityAIBase
{
    /** The entity the AI instance has been applied to */
    public final EntityLiving entityHost;
    /** The entity (as a RangedAttackMob) the AI instance has been applied to. */
    public final IRangedAttackMob rangedAttackEntityHost;

    //Move speed
    public double entityMoveSpeed;

    //Attack time
    public int minRangedAttackTime;
    public int maxRangedAttackTime;

    //Range
    public float attackRange;
    public float attackRangeSQ;

    //Internal tracked values
    private int lostTargetTimer;
    private int rangedAttackTime;
    private EntityLivingBase attackTarget;

    public EntityAIArrowAttackOverride(IRangedAttackMob host, EntityOverrideArrowAttack.ArrowAttackSettings settings)
    {
        this.rangedAttackTime = -1;

        if (!(host instanceof EntityLivingBase))
        {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        else
        {
            this.rangedAttackEntityHost = host;
            this.entityHost = (EntityLiving) host;
            this.entityMoveSpeed = settings.entityMoveSpeed;
            this.minRangedAttackTime = settings.minRangedAttackTime;
            this.maxRangedAttackTime = settings.maxRangedAttackTime;
            this.attackRange = settings.attackRange;
            this.attackRangeSQ = attackRange * attackRange;
            this.setMutexBits(3);
        }
    }

    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else
        {
            this.attackTarget = entitylivingbase;
            return true;
        }
    }

    @Override
    public boolean continueExecuting()
    {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }

    @Override
    public void resetTask()
    {
        this.attackTarget = null;
        this.lostTargetTimer = 0;
        this.rangedAttackTime = -1;
    }

    @Override
    public void updateTask()
    {
        final double distanceSQ = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        final boolean canSeeTarget = this.entityHost.getEntitySenses().canSee(this.attackTarget);

        //If can't see target increase lost timer
        if (canSeeTarget)
        {
            ++this.lostTargetTimer;
        }
        //Reset timer if we can see target
        else
        {
            this.lostTargetTimer = 0;
        }

        //Clear path if we are close enough but lost sight
        if (distanceSQ <= (double) this.attackRangeSQ && this.lostTargetTimer >= 20)
        {
            this.entityHost.getNavigator().clearPathEntity();
        }
        //Update path TODO maybe delay?
        else
        {
            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }

        //Look at target TODO allow disable
        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);

        //Do Attack
        if (--this.rangedAttackTime == 0)
        {
            if (distanceSQ > (double) this.attackRangeSQ || !canSeeTarget)
            {
                return;
            }

            //Scale attack power by distance (think this is the speed of the arrow)
            float scale = MathHelper.sqrt_double(distanceSQ) / this.attackRange;

            //Scale attack power modifier between 0.1 - 1
            float actualAttackPower = scale;
            if (scale < 0.1F)
            {
                actualAttackPower = 0.1F;
            }
            else if (actualAttackPower > 1.0F)
            {
                actualAttackPower = 1.0F;
            }

            //do attack
            this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, actualAttackPower);

            //Reset attack timer
            this.rangedAttackTime = MathHelper.floor_float(scale * (float) (this.maxRangedAttackTime - this.minRangedAttackTime) + (float) this.minRangedAttackTime);
        }
        //Reset
        else if (this.rangedAttackTime < 0)
        {
            float scale = MathHelper.sqrt_double(distanceSQ) / this.attackRange;

            //Reset attack timer
            this.rangedAttackTime = MathHelper.floor_float(scale * (float) (this.maxRangedAttackTime - this.minRangedAttackTime) + (float) this.minRangedAttackTime);
        }
    }
}