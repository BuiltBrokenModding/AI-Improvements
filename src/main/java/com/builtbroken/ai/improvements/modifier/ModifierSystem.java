package com.builtbroken.ai.improvements.modifier;

import com.builtbroken.ai.improvements.AIImprovements;
import com.builtbroken.ai.improvements.AnimalConfigSection;
import com.builtbroken.ai.improvements.ConfigMain;
import com.builtbroken.ai.improvements.FilteredConfigValue;
import com.builtbroken.ai.improvements.FixedLookController;
import com.builtbroken.ai.improvements.modifier.editor.FilteredRemove;
import com.builtbroken.ai.improvements.modifier.editor.GenericRemove;
import com.builtbroken.ai.improvements.modifier.filters.FilterLayer;
import com.builtbroken.ai.improvements.modifier.filters.FilterResult;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowSchoolLeaderGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/3/2019.
 */
@Mod.EventBusSubscriber(modid = AIImprovements.DOMAIN)
public class ModifierSystem
{

	public static final FilterLayer editor = new FilterLayer(null);
	public static final ModifierLevel mobEntityEditor = ModifierLevel.newFilter(entity -> entity instanceof MobEntity);
	public static final ModifierLevel fishEditor = ModifierLevel.newFilter(entity -> entity instanceof AbstractFishEntity);
	public static final ModifierLevel squidEditor = ModifierLevel.newFilter(entity -> entity instanceof SquidEntity);
	public static final ModifierLevel cowEditor = ModifierLevel.newFilter(entity -> entity instanceof CowEntity);
	public static final ModifierLevel chickenEditor = ModifierLevel.newFilter(entity -> entity instanceof ChickenEntity);
	public static final ModifierLevel pigEditor = ModifierLevel.newFilter(entity -> entity instanceof PigEntity);
	public static final ModifierLevel sheepEditor = ModifierLevel.newFilter(entity -> entity instanceof SheepEntity);

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		editor.handle(event.getEntity());
	}

	public static void init()
	{
		editor.add(mobEntityEditor);

		//Generic remove calls
		mobEntityEditor.goalEditor.add(new FilteredRemove(goal -> goal instanceof LookAtGoal, ConfigMain.CONFIG.removeLookGoal));
		mobEntityEditor.goalEditor.add(new FilteredRemove(goal -> goal instanceof LookRandomlyGoal, ConfigMain.CONFIG.removeLookRandom));
		mobEntityEditor.filters.add(entity -> replaceLookHelper((MobEntity) entity));

		//Fish remove calls
		mobEntityEditor.filters.add(fishEditor);
		fishEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof RandomSwimmingGoal, ConfigMain.CONFIG.removeFishSwim));
		fishEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof AvoidEntityGoal, ConfigMain.CONFIG.removeFishAvoidPlayer));
		fishEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof PanicGoal, ConfigMain.CONFIG.removeFishPanic));
		fishEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof FollowSchoolLeaderGoal, ConfigMain.CONFIG.removeFishFollowLeader));
		fishEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof PufferfishEntity.PuffGoal, ConfigMain.CONFIG.removeFishFollowLeader));

		//Squid
		mobEntityEditor.filters.add(squidEditor);
		squidEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof SquidEntity.MoveRandomGoal, ConfigMain.CONFIG.removeRandomMove));
		squidEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof SquidEntity.FleeGoal, ConfigMain.CONFIG.removeRandomMove));

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
		sheepEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof EatGrassGoal, ConfigMain.CONFIG.removeSheepEatBlock));
	}

	private static FilterResult replaceLookHelper(MobEntity living)
	{
		FilteredConfigValue replaceLookController = ConfigMain.CONFIG.replaceLookController;

		//Only replace vanilla look helper to avoid overlapping mods
		if (replaceLookController.configValue().get() && (living.getLookControl() == null || living.getLookControl().getClass() == LookController.class))
		{
			boolean isAllowlist = replaceLookController.isAllowlist().get();
			String registryName = living.getType().getRegistryName().toString();

			//if it's an allowlist, mobs on the filter list have their look helper replaced
			//if it's not an allowlist (denylist), the mobs NOT on the filter list have their look helper replaced
			if (isAllowlist != replaceLookController.filterList().get().contains(registryName))
				return FilterResult.DID_NOTHING;

			//Get old so we can copy data
			final LookController oldHelper = living.getLookControl();

			//Set new
			living.lookControl = new FixedLookController(living);

			//Instance of check may look unneeded but some mods do stupid things
			if (living.getLookControl() instanceof FixedLookController)
			{
				((FixedLookController) living.getLookControl()).copyDataIntoSelf(oldHelper);
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
		animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof SwimGoal, configs.removeFloat()));
		animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof PanicGoal, configs.removePanic()));
		animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof BreedGoal, configs.removeBreed()));
		animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof TemptGoal, configs.removeTempt()));
		animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof FollowParentGoal, configs.removeFollowParent()));
		animalEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof WaterAvoidingRandomWalkingGoal, configs.removeStroll()));
	}
}
