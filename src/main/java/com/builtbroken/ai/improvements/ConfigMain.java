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

    //Global
    public final BooleanValue allowRemoveCalls;
    public final BooleanValue enableCallBubbling;

    //Generic mob
    public final BooleanValue removeLookGoal;
    public final BooleanValue removeLookRandom;
    public final BooleanValue replaceLookController;

    //Fish
    public final BooleanValue removeFishSwim;
    public final BooleanValue removeFishAvoidPlayer;
    public final BooleanValue removeFishPanic;
    public final BooleanValue removeFishFollowLeader;
    public final BooleanValue removeFishPuff;

    //Squid
    public final BooleanValue removeSquidFlee;
    public final BooleanValue removeRandomMove;

    static
    {
        Pair<ConfigMain, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigMain::new);

        CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    ConfigMain(ForgeConfigSpec.Builder builder)
    {
        builder.comment("Entity Settings").push("entity");

        //General Settings
        builder.comment("General").push("general");
        allowRemoveCalls = builder
                .comment("Allow AI tasks to be removed from entities at runtime. If this is disable no per mob or per mob type removes will run.")
                .define("allow_remove_calls", true);

        enableCallBubbling = builder
                .comment("Allows repeat remove calls to bubble to the top of the list to improve performance of repeat mob spawning.")
                .define("enable_call_bubbling", true);

        builder.pop();

        //Anything extending EntityMob (Animals, NPCS, Monsters, etc... basically everything)
        builder.comment("Entity Mob").push("mob");
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
        builder.comment("Entity Fish").push("fish");

        removeFishSwim = builder
                .comment("Remove the fish's random swimming pathfinder. This will cause fish to stay in position more often.")
                .define("remove_swim", false);

        removeFishPanic = builder
                .comment("Remove the fish's panic pathfinder. This will cause fish to not run away.")
                .define("remove_panic", false);

        removeFishAvoidPlayer = builder
                .comment("Remove the fish's AI task to avoid players.")
                .define("remove_avoid_player", false);

        removeFishFollowLeader = builder
                .comment("Remove the fish's AI task to follow a leader fish to act as a group of fish.")
                .define("remove_follow_leader", false);

        removeFishPuff = builder
                .comment("Remove the fish's AI task to puff up when entities are nearby")
                .define("remove_puff", false);

        builder.pop();

        //Anything extending AbstractFish
        builder.comment("Squid Fish").push("squid");

        removeSquidFlee = builder
                .comment("Remove the squid's flee pathfinder. This will cause squid to not run away.")
                .define("remove_flee", false);

        removeRandomMove = builder
                .comment("Remove the squid's random movement pathfinder. This will cause squid to swim around randomly.")
                .define("remove_random_move", false);

        builder.pop();

        builder.pop();
    }
}
