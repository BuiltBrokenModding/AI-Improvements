package com.builtbroken.ai.improvements;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by Dark(DarkGuardsman, Robert) on 2019-08-01.
 */
public class ConfigMain
{

    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final ConfigMain CONFIG;

    public final BooleanValue allowRemoveCalls;

    public final BooleanValue removeLookGoal;
    public final BooleanValue removeLookRandom;
    public final BooleanValue replaceLookController;

    public final BooleanValue removeFishSwim;
    public final BooleanValue removeFishAvoidPlayer;
    public final BooleanValue removeFishPanic;

    static
    {
        Pair<ConfigMain, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigMain::new);

        CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    ConfigMain(ForgeConfigSpec.Builder builder)
    {
        //General Settings
        builder.comment("Entity Mob").push("entity.mob");
        allowRemoveCalls = builder
                .comment("Allow AI tasks to be removed from entities at runtime. If this is disable no per mob or per mob type removes will run.")
                .define("allow_remove_calls", true);

        builder.pop();

        //Anything extending EntityMob (Animals, NPCS, Monsters, etc... basically everything)
        builder.comment("Entity Mob").push("entity.mob");
        removeLookGoal = builder
                .comment("Remove the look at goal (player or attack target) AI task. This will cause AIs to not face targets or walking directions.")
                .define("remove_look_goal", false);

        removeLookRandom = builder
                .comment("Remove the look at random position AI task. This will cause AIs to feel a little lifeless as they do not animate head movement while idle.")
                .define("remove_look_random", false);

        replaceLookController = builder
                .comment("Replaces the default look controller with a version featuring cached tan math improving performance. " +
                        "Only works on vanilla style mobs, if a mod overrides the look controller it will skip.")
                .define("replace_look_controller", true);

        builder.pop();


        //Anything extending AbstractFish
        builder.comment("Entity Fish").push("entity.fish");

        removeFishSwim = builder
                .comment("Remove the fish's random swimming pathfinder. This will cause fish to stay in position more often.")
                .define("remove_swim", false);

        removeFishPanic = builder
                .comment("Remove the fish's panic pathfinder. This will cause fish to not run away.")
                .define("remove_panic", false);

        removeFishAvoidPlayer = builder
                .comment("Remove the fish's AI task to avoid players.")
                .define("remove_avoid_player", false);

        builder.pop();
    }
}
