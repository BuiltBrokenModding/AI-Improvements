package com.builtbroken.ai.improvements.modifier;

import com.builtbroken.ai.improvements.AIImprovements;
import com.builtbroken.ai.improvements.ConfigMain;
import com.builtbroken.ai.improvements.FixedLookController;
import com.builtbroken.ai.improvements.modifier.editor.GenericRemove;
import com.builtbroken.ai.improvements.modifier.filters.FilterLayer;
import com.builtbroken.ai.improvements.modifier.filters.FilterResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Pufferfish;
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
    public static final ModifierLevel mobEntityEditor = ModifierLevel.newFilter(entity -> entity instanceof Mob);
    public static final ModifierLevel fishEditor = ModifierLevel.newFilter(entity -> entity instanceof AbstractFish);
    public static final ModifierLevel squidEditor = ModifierLevel.newFilter(entity -> entity instanceof Squid);

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        editor.handle(event.getEntity());
    }

    public static void init()
    {
        editor.add(mobEntityEditor);

        //Generic remove calls
        mobEntityEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof LookAtPlayerGoal, ConfigMain.CONFIG.removeLookGoal));
        mobEntityEditor.goalEditor.add(new GenericRemove(goal -> goal instanceof RandomLookAroundGoal, ConfigMain.CONFIG.removeLookRandom));
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

    }


    private static FilterResult replaceLookHelper(Mob living)
    {
        //Only replace vanilla look helper to avoid overlapping mods
        if (ConfigMain.CONFIG.replaceLookController.get() && (living.getLookControl() == null || living.getLookControl().getClass() == LookControl.class))
        {
            //Get old so we can copy data
            final LookControl oldHelper = living.getLookControl();

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
