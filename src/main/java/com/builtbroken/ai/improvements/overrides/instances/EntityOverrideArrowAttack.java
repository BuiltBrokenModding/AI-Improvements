package com.builtbroken.ai.improvements.overrides.instances;

import com.builtbroken.ai.improvements.AIImprovements;
import com.builtbroken.ai.improvements.ai.EntityAIArrowAttackOverride;
import com.builtbroken.ai.improvements.overrides.EntityOverride;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAITasks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/20/2017.
 */
public class EntityOverrideArrowAttack extends EntityOverride
{
    HashMap<String, ArrowAttackSettings> arrowAttackSettings = new HashMap();

    public EntityOverrideArrowAttack()
    {
        super("minecraft.attack.arrow", "Allows overriding default settings for arrow range attacks " +
                "used by skeleton. " +
                "Might work on modded entities but depends on implementation by each mod.");
    }

    @Override
    public void applyChanges(Entity entity)
    {
        if (entity instanceof IRangedAttackMob && entity instanceof EntityLivingBase)
        {
            String id = EntityList.getEntityString(entity);
            if (arrowAttackSettings.containsKey(id))
            {
                ArrowAttackSettings settings = arrowAttackSettings.get(id);
                if (settings != null)
                {
                    ListIterator it = ((EntityCreature) entity).tasks.taskEntries.listIterator();
                    while (it.hasNext())
                    {
                        EntityAITasks.EntityAITaskEntry obj = (EntityAITasks.EntityAITaskEntry) it.next();
                        if (obj.action.getClass() == EntityAIArrowAttack.class)
                        {
                            obj.action = new EntityAIArrowAttackOverride((IRangedAttackMob) entity, settings);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadOverAllSettings()
    {

    }

    @Override
    public void loadEntitySettings(String id)
    {
        //First run is raw entity, second is egg spawn rng
        for(int i = 0; i < 2 && !arrowAttackSettings.containsKey(id); i++) //TODO add more cases, e.g. spawning in different worlds and settings
        {
            //Build entity
            Entity entity = EntityList.createEntityByName(id, AIImprovements.fakeWorld);
            if (entity instanceof EntityCreature)
            {
                ArrowAttackSettings settings = null;

                //Trigger spawn egg in order to catch cases where entities can spawn with different weapons
                if (i == 1)
                {
                    ((EntityLiving)entity).onSpawnWithEgg(null);
                }

                //Collect setting data
                Iterator it = ((EntityCreature) entity).tasks.taskEntries.iterator();
                while (it.hasNext())
                {
                    EntityAITasks.EntityAITaskEntry obj = (EntityAITasks.EntityAITaskEntry) it.next();
                    if (obj.action.getClass() == EntityAIArrowAttack.class)
                    {
                        if (settings == null)
                        {
                            EntityAIArrowAttack action = (EntityAIArrowAttack) obj.action;
                            settings = new ArrowAttackSettings();

                            settings.entityMoveSpeed = action.entityMoveSpeed;
                            settings.minRangedAttackTime = action.field_96561_g;
                            settings.maxRangedAttackTime = action.maxRangedAttackTime;
                            settings.attackRange = action.field_96562_i;
                        }
                        else
                        {
                            AIImprovements.LOGGER.warn("Entity[" + id + "] contains more than one instance of EntityAIArrowAttack when only 1 can run at a time." +
                                    " This will cause issue with the AI and result in all being replaced by the same data. This is likely a bug or an oversight by" +
                                    " the entity's developer and should be reported as a potential bug.");
                            //TODO warning, shouldn't happen as we can only have one ranged attack per entity
                            break;
                        }
                    }
                }

                if (settings != null)
                {
                    final String category = id + "#rangedAttack";

                    settings.minRangedAttackTime = configuration.getInt("minAttackTime", category, settings.minRangedAttackTime, 0, 1200, "Shortest time in ticks (20 ticks a second) between attacks");
                    settings.maxRangedAttackTime = configuration.getInt("maxAttackTime", category, settings.maxRangedAttackTime, 0, 1200,
                            "Longest time in ticks (20 ticks a second) between attacks, scaled using (((distanceToTargetSQ / attackRange) * (maxTime - minTime)) + minTime) resulting in faster attacks the closer a target gets.");
                    settings.entityMoveSpeed = configuration.getFloat("moveSpeedTowardsTarget", category, (float) settings.entityMoveSpeed, 0, 2, "Speed of the entity when moving closer to the target.");
                    settings.attackRange = configuration.getFloat("range", category, settings.attackRange, 0, 400, "Range to attack targets, is limited by power of an arrow and target selector range");
                    if (configuration.getBoolean("enable", category, false, "allows enabling replacement for the entity class's attack AI and settings"))
                    {
                        arrowAttackSettings.put(id, settings);
                    }
                }
            }
        }
    }

    @Override
    public boolean canAffectEntity(Entity entity)
    {
        return entity instanceof IRangedAttackMob && entity instanceof EntityLivingBase;
    }

    public final class ArrowAttackSettings
    {
        public double entityMoveSpeed;

        public int minRangedAttackTime;
        public int maxRangedAttackTime;

        public float attackRange;
    }
}
