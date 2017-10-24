package com.builtbroken.ai.improvements.overrides;

import com.builtbroken.ai.improvements.AIImprovements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/19/2017.
 */
public class EntityOverride
{
    public final String id;
    public String description;

    public boolean enabled = true;

    protected Configuration configuration;

    protected List<String> entityByIdToEffect = new ArrayList();

    public EntityOverride(String id, String description)
    {
        this.id = id;
        this.description = description;
    }

    public void applyChanges(Entity entity)
    {

    }

    public void loadSettings(Configuration configuration)
    {
        this.configuration = configuration;
        this.configuration.load();
        loadOverAllSettings();

        Set<String> ids = EntityList.func_151515_b();
        for (String id : ids)
        {
            if (canAffectEntity(id))
            {
                loadEntitySettings(id);
            }
        }
        this.configuration.save();
    }

    public boolean canAffectEntity(String id)
    {
        if (entityByIdToEffect.contains(id))
        {
            return true;
        }
        try
        {
            Entity entity = createEntityByName(id, AIImprovements.fakeWorld);
            if (entity != null)
            {
                return canAffectEntity(entity);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static Entity createEntityByName(String id, World world)
    {
        Entity entity = null;

        Class oclass = (Class) EntityList.stringToClassMapping.get(id);
        try
        {

            if (oclass != null)
            {
                Constructor<Entity> constructor = oclass.getConstructor(new Class[]{World.class});
                constructor.setAccessible(true);
                if (constructor != null)
                {
                    entity = constructor.newInstance(new Object[]{world});
                }
            }
        }
        catch (NoSuchMethodException e)
        {
            AIImprovements.LOGGER.error("EntityOverride: Couldn't find constructor 'Entity(World)' for entity[" + id + ", " + oclass + "] in order to test for AI tasks");
            if (e.getCause() != null)
            {
                e.getCause().printStackTrace();
            }
        }
        catch (InstantiationException e)
        {
            AIImprovements.LOGGER.error("EntityOverride: Failed to use constructor 'Entity(World)' for entity[" + id + ", " + oclass + "] in order to test for AI tasks");
            if (e.getCause() != null)
            {
                e.getCause().printStackTrace();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        return entity;
    }

    public boolean canAffectEntity(Entity entity)
    {
        return false;
    }

    public void loadOverAllSettings()
    {

    }

    public void loadEntitySettings(String id)
    {

    }
}
