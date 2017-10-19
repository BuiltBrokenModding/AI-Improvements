package com.builtbroken.ai.improvements.ai;

import com.builtbroken.ai.improvements.overrides.instances.EntityOverrideAttackOnCollide;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIAttackOnCollideOverride extends EntityAIBase
{
    public final World worldObj;
    public final EntityCreature attacker;
    public int attackTimeTrigger = 20;

    /** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
    int attackTick;
    /** The speed with which the mob will approach the target */
    double speedTowardsTarget;
    /** When true, the mob will continue chasing its target, even if it can't find a path to them right now. */
    boolean longMemory;
    /** The PathEntity of our entity. */
    PathEntity entityPathEntity;

    Class classTarget;

    private int pathUpdateTimer;
    private double targetX;
    private double targetY;
    private double targetZ;

    private int failedPathFindingPenalty;

    public EntityAIAttackOnCollideOverride(EntityCreature host, Class classToAttack, double moveSpeed, boolean longMemory)
    {
        this(host, moveSpeed, longMemory);
        this.classTarget = classToAttack;
    }

    public EntityAIAttackOnCollideOverride(EntityCreature host, double moveSpeed, boolean longMemory)
    {
        this.attacker = host;
        this.worldObj = host.worldObj;
        this.speedTowardsTarget = moveSpeed;
        this.longMemory = longMemory;
        this.setMutexBits(3);
    }

    public EntityAIAttackOnCollideOverride(EntityCreature entity, EntityOverrideAttackOnCollide.AttackSettings settings)
    {
        this(entity, settings.classTarget, settings.speedTowardsTarget, settings.longMemory);
        attackTimeTrigger = settings.attackTimeTrigger;
    }

    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (isTargetValid())
        {
            if (--this.pathUpdateTimer <= 0)
            {
                this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
                this.pathUpdateTimer = 4 + this.attacker.getRNG().nextInt(7);
                return this.entityPathEntity != null;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean continueExecuting()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        return !isTargetValid() ? false : (!this.longMemory ? !this.attacker.getNavigator().noPath() : this.attacker.isWithinHomeDistance(MathHelper.floor_double(entitylivingbase.posX), MathHelper.floor_double(entitylivingbase.posY), MathHelper.floor_double(entitylivingbase.posZ)));
    }

    @Override
    public void startExecuting()
    {
        this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
        this.pathUpdateTimer = 0;
    }

    public boolean isTargetValid()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else if (this.classTarget != null && !this.classTarget.isAssignableFrom(entitylivingbase.getClass()))
        {
            return false;
        }
        return true;
    }

    public void resetTask()
    {
        this.attacker.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (isTargetValid())
        {
            if (lookAtTargetWhileAttacking())
            {
                this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
            }

            double distance = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.boundingBox.minY, entitylivingbase.posZ);


            --this.pathUpdateTimer;

            if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.pathUpdateTimer <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F))
            {
                this.targetX = entitylivingbase.posX;
                this.targetY = entitylivingbase.boundingBox.minY;
                this.targetZ = entitylivingbase.posZ;
                this.pathUpdateTimer = failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);

                if (this.attacker.getNavigator().getPath() != null)
                {
                    PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
                    if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1)
                    {
                        failedPathFindingPenalty = 0;
                    }
                    else
                    {
                        failedPathFindingPenalty += 10;
                    }
                }
                else
                {
                    failedPathFindingPenalty += 10;
                }

                if (distance > 1024.0D)
                {
                    this.pathUpdateTimer += 10;
                }
                else if (distance > 256.0D)
                {
                    this.pathUpdateTimer += 5;
                }

                if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget))
                {
                    this.pathUpdateTimer += 15;
                }
            }

            //Tick attack timer
            this.attackTick = Math.max(this.attackTick - 1, 0);

            //if in range and cool down, do attack
            if (distance <= getAttackDistanceSQ() && this.attackTick <= 0)
            {
                //reset attack timer
                this.attackTick = attackTimeTrigger;

                //Swing item
                if (this.attacker.getHeldItem() != null)
                {
                    this.attacker.swingItem();
                }

                //Do attack
                this.attacker.attackEntityAsMob(entitylivingbase);
            }
        }
    }

    protected boolean lookAtTargetWhileAttacking()
    {
        return true; //TODO add config to disable
    }

    protected double getAttackDistanceSQ()
    {
        if (this.attacker.getAttackTarget() != null)
        {
            return (double) (this.attacker.width * 2.0F * this.attacker.width * 2.0F + this.attacker.getAttackTarget().width);
        }
        return 1;
    }
}