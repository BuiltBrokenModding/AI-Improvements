package com.builtbroken.ai.improvements.modifier;

import com.builtbroken.ai.improvements.AIImprovements;
import com.builtbroken.ai.improvements.ConfigMain;
import com.builtbroken.ai.improvements.ConfigMain.AnimalConfigSection;
import com.builtbroken.ai.improvements.ConfigMain.FilteredConfigValue;
import com.builtbroken.ai.improvements.FixedLookControl;
import com.builtbroken.ai.improvements.modifier.editor.FilteredRemove;
import com.builtbroken.ai.improvements.modifier.editor.GenericRemove;
import com.builtbroken.ai.improvements.modifier.filters.FilterLayer;
import com.builtbroken.ai.improvements.modifier.filters.FilterResult;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Squid;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/3/2019.
 */
@Mod.EventBusSubscriber(modid = AIImprovements.DOMAIN)
public class ModifierSystem
{

    public static final FilterLayer editor = new FilterLayer(null);
    public static final ModifierLevel mobEntityEditor = ModifierLevel.newFilter(entity -> entity instanceof Mob);
    public static final ModifierLevel fishEditor = ModifierLevel.newFilter(entity -> entity instanceof AbstractFish);
    public static final ModifierLevel squidEditor = ModifierLevel.newFilter(entity -> entity instanceof Squid);
    public static final ModifierLevel cowEditor = ModifierLevel.newFilter(entity -> entity instanceof Cow);
    public static final ModifierLevel chickenEditor = ModifierLevel.newFilter(entity -> entity instanceof Chicken);
    public static final ModifierLevel pigEditor = ModifierLevel.newFilter(entity -> entity instanceof Pig);
    public static final ModifierLevel sheepEditor = ModifierLevel.newFilter(entity -> entity instanceof Sheep);

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        editor.handle(event.getEntity());
    }

    @SubscribeEvent
    public static void onSpawn(LivingSpawnEvent event)
    {
        editor.handle(event.getEntity());
    }

    public static void init()
    {
        editor.add(mobEntityEditor);

        //Generic remove calls
        mobEntityEditor.goalEditor.add(new FilteredRemove(goal -> goal instanceof LookAtPlayerGoal, ConfigMain.CONFIG.removeLookGoal));
        mobEntityEditor.goalEditor.add(new FilteredRemove(goal -> goal instanceof RandomLookAroundGoal, ConfigMain.CONFIG.removeLookRandom));
        mobEntityEditor.filters.add(entity -> replaceLookHelper((Mob) entity));

        //Fish remove calls
        mobEntityEditor.filters.add(fishEditor);
        fishEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof RandomSwimmingGoal, ConfigMain.CONFIG.removeFishSwim));
        fishEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof AvoidEntityGoal, ConfigMain.CONFIG.removeFishAvoidPlayer));
        fishEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof PanicGoal, ConfigMain.CONFIG.removeFishPanic));
        fishEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof FollowFlockLeaderGoal, ConfigMain.CONFIG.removeFishFollowLeader));
        fishEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof Pufferfish.PufferfishPuffGoal, ConfigMain.CONFIG.removeFishFollowLeader));

        //Squid
        mobEntityEditor.filters.add(squidEditor);
        squidEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof Squid.SquidRandomMovementGoal, ConfigMain.CONFIG.removeRandomMove));
        squidEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof Squid.SquidFleeGoal, ConfigMain.CONFIG.removeRandomMove));

        //Cow
        mobEntityEditor.filters.add(cowEditor);
        populateAnimalEditor(cowEditor, ConfigMain.CONFIG.cow);

        //Chicken
        mobEntityEditor.filters.add(chickenEditor);
        populateAnimalEditor(chickenEditor, ConfigMain.CONFIG.chicken);

        //Pig
        mobEntityEditor.filters.add(pigEditor);
        populateAnimalEditor(pigEditor, ConfigMain.CONFIG.pig);

        //Sheep
        mobEntityEditor.filters.add(sheepEditor);
        populateAnimalEditor(sheepEditor, ConfigMain.CONFIG.sheep);
        sheepEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof EatBlockGoal, ConfigMain.CONFIG.removeSheepEatBlock));
    }

    private static FilterResult replaceLookHelper(Mob living)
    {
        FilteredConfigValue replaceLookController = ConfigMain.CONFIG.replaceLookController;

        //Only replace vanilla look helper to avoid overlapping mods
        if (replaceLookController.configValue().get() && (living.getLookControl() == null || living.getLookControl().getClass() == LookControl.class))
        {
            //if it's an allowlist, mobs on the filter list have their look helper replaced
            //if it's not an allowlist (denylist), the mobs NOT on the filter list have their look helper replaced
            if (replaceLookController.isFiltered(ForgeRegistries.ENTITIES.getKey(living.getType()).toString()))
            {
                return FilterResult.DID_NOTHING;
            }

            //Get old so we can copy data
            final LookControl oldHelper = living.getLookControl();

            //Set new
            living.lookControl = new FixedLookControl(living);

            //Instance of check may look unneeded but some mods do stupid things
            if (living.getLookControl() instanceof FixedLookControl flc)
            {
                flc.copyDataIntoSelf(oldHelper);
                return FilterResult.MODIFIED;
            }
            else
            {
                //TODO error/warning in console, then mark this entity as unusable for future checks
            }
        }
        return FilterResult.DID_NOTHING;
    }

    private static void populateAnimalEditor(ModifierLevel animalEditor, AnimalConfigSection configs) {
        animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof FloatGoal, configs.removeFloat()));
        animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof PanicGoal, configs.removePanic()));
        animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof BreedGoal, configs.removeBreed()));
        animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof TemptGoal, configs.removeTempt()));
        animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof FollowParentGoal, configs.removeFollowParent()));
        animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof WaterAvoidingRandomStrollGoal, configs.removeStroll()));
    }
}
