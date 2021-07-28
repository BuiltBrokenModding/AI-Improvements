package com.builtbroken.ai.improvements.modifier;

import com.builtbroken.ai.improvements.AIImprovements;
import com.builtbroken.ai.improvements.ConfigMain;
import com.builtbroken.ai.improvements.FixedLookController;
import com.builtbroken.ai.improvements.modifier.editor.GenericRemove;
import com.builtbroken.ai.improvements.modifier.filters.FilterLayer;
import com.builtbroken.ai.improvements.modifier.filters.FilterResult;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.FollowSchoolLeaderGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
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

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        editor.handle(event.getEntity());
    }

    public static void init()
    {
        editor.add(mobEntityEditor);

        //Generic remove calls
        mobEntityEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof LookAtGoal, ConfigMain.CONFIG.removeLookGoal));
        mobEntityEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof LookRandomlyGoal, ConfigMain.CONFIG.removeLookRandom));
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

    }


    private static FilterResult replaceLookHelper(MobEntity living)
    {
        //Only replace vanilla look helper to avoid overlapping mods
        if (ConfigMain.CONFIG.replaceLookController.get() && (living.getLookControl() == null || living.getLookControl().getClass() == LookController.class))
        {
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
}
