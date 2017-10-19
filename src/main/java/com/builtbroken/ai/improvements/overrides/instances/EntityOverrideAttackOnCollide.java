package com.builtbroken.ai.improvements.overrides.instances;

import com.builtbroken.ai.improvements.AIImprovements;
import com.builtbroken.ai.improvements.ai.EntityAIAttackOnCollideOverride;
import com.builtbroken.ai.improvements.overrides.EntityOverride;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAITasks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/19/2017.
 */
public class EntityOverrideAttackOnCollide extends EntityOverride
{
    HashMap<String, HashMap<Class, AttackSettings>> settings = new HashMap();

    public EntityOverrideAttackOnCollide()
    {
        super("minecraft.attack.collide", "Allows overriding default settings for close range attacks " +
                "used by zombies, pig zombies, or any melee monster. " +
                "Might work on modded entities but depends on implementation by each mod.");
    }

    @Override
    public void applyChanges(Entity entity)
    {
        if (entity instanceof EntityCreature)
        {
            String id = EntityList.getEntityString(entity);
            if (settings.containsKey(id))
            {
                HashMap<Class, AttackSettings> settingsHashMap = settings.get(id);
                if (settingsHashMap != null && !settingsHashMap.isEmpty())
                {
                    ListIterator it = ((EntityCreature) entity).tasks.taskEntries.listIterator();
                    while (it.hasNext())
                    {
                        EntityAITasks.EntityAITaskEntry obj = (EntityAITasks.EntityAITaskEntry) it.next();
                        if (obj.action.getClass() == EntityAIAttackOnCollide.class)
                        {
                            Class attackClass = ((EntityAIAttackOnCollide) obj.action).classTarget;
                            if (settingsHashMap.containsKey(attackClass))
                            {
                                AttackSettings settings = settingsHashMap.get(attackClass);
                                obj.action = new EntityAIAttackOnCollideOverride((EntityCreature) entity, settings);
                            }
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
        //Build entity
        Entity entity = EntityList.createEntityByName(id, AIImprovements.fakeWorld);
        if (entity instanceof EntityCreature)
        {
            HashMap<Class, AttackSettings> settingsHashMap = new HashMap();

            //Collect setting data
            Iterator it = ((EntityCreature) entity).tasks.taskEntries.iterator();
            while (it.hasNext())
            {
                EntityAITasks.EntityAITaskEntry obj = (EntityAITasks.EntityAITaskEntry) it.next();
                if (obj.action.getClass() == EntityAIAttackOnCollide.class)
                {
                    EntityAIAttackOnCollide action = (EntityAIAttackOnCollide) obj.action;
                    AttackSettings settings = new AttackSettings();

                    settings.classTarget = action.classTarget;
                    settings.attackTimeTrigger = 20;
                    settings.speedTowardsTarget = action.speedTowardsTarget;
                    settings.longMemory = action.longMemory;

                    if (!settingsHashMap.containsKey(settings.classTarget))
                    {
                        settingsHashMap.put(settings.classTarget, settings);
                    }
                    else
                    {
                        //TODO warning, shouldn't happen
                    }
                }
            }

            HashMap<Class, AttackSettings> actualSettingMap = new HashMap();
            for (AttackSettings settings : settingsHashMap.values())
            {
                String category = id + "#attacking#";
                if (settings.classTarget != null)
                {
                    String class_id = (String) EntityList.classToStringMapping.get(settings.classTarget);
                    if (class_id != null)
                    {
                        category += class_id;
                    }
                    else
                    {
                        category += "[" + settings.classTarget.toString().replace("class", "").replace(".", "_").trim() + "]";
                    }
                }
                else
                {
                    category += "any";
                }

                settings.attackTimeTrigger = configuration.getInt("attackTime", category, settings.attackTimeTrigger, 0, 1200, "Time in ticks (20 ticks a second) between attacks");
                settings.speedTowardsTarget = configuration.getFloat("moveSpeedTowardsTarget", category, (float) settings.speedTowardsTarget, 0, 2, "Speed of the entity when moving closer to the target.");
                settings.longMemory = configuration.getBoolean("useLongMemory", category, settings.longMemory, "Allows the entity to keep tracking a target even if it can't walk to the target.");
                if (configuration.getBoolean("enable", category, false, "allows enabling replacement for the entity class's attack AI and settings"))
                {
                    actualSettingMap.put(settings.classTarget, settings);
                }
            }

            if (!actualSettingMap.isEmpty())
            {
                settings.put(id, actualSettingMap);
            }
        }
    }

    @Override
    public boolean canAffectEntity(Entity entity)
    {
        return entity instanceof EntityCreature;
    }

    public final class AttackSettings
    {
        public int attackTimeTrigger;
        public double speedTowardsTarget;
        public boolean longMemory;
        public Class classTarget;
    }
}
