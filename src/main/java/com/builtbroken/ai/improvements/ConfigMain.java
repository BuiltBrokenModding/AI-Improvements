package com.builtbroken.ai.improvements;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

/**
 * Created by Dark(DarkGuardsman, Robert) on 2019-08-01.
 */
public class ConfigMain
{
    public record AnimalConfigSection(BooleanValue removeFloat, BooleanValue removePanic, BooleanValue removeBreed, BooleanValue removeTempt, BooleanValue removeFollowParent, BooleanValue removeStroll) {}

    public record FilteredConfigValue(BooleanValue configValue, BooleanValue isAllowlist, ConfigValue<List<? extends String>> filterList) {}

    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final ConfigMain CONFIG;

    //Global
    public final BooleanValue allowRemoveCalls;
    public final BooleanValue enableCallBubbling;

    //Generic mob
    public final FilteredConfigValue removeLookGoal;
    public final FilteredConfigValue removeLookRandom;
    public final FilteredConfigValue replaceLookController;

    //Fish
    public final BooleanValue removeFishSwim;
    public final BooleanValue removeFishAvoidPlayer;
    public final BooleanValue removeFishPanic;
    public final BooleanValue removeFishFollowLeader;
    public final BooleanValue removeFishPuff;

    //Squid
    public final BooleanValue removeSquidFlee;
    public final BooleanValue removeRandomMove;

    //Animals
    public final AnimalConfigSection cow;
    public final AnimalConfigSection chicken;
    public final AnimalConfigSection pig;
    public final AnimalConfigSection sheep;
    public final BooleanValue removeSheepEatBlock;

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

        removeLookGoal = createFilteredConfigValue(builder, "Remove Look Goal", "Remove the look at goal (player or attack target) AI task. This will cause AIs to not face targets or walking directions.", "remove_look_goal", false);
        removeLookRandom = createFilteredConfigValue(builder, "Remove Look Random", "Remove the look at random position AI task. This will cause AIs to feel a little lifeless as they do not animate head movement while idle.", "remove_look_random", false);
        replaceLookController = createFilteredConfigValue(builder, "Replace Look Controller", "Replaces the default look controller with a version featuring cached tan math improving performance. Only works on vanilla style mobs, if a mod overrides the look controller it will skip.", "replace_look_controller", true);

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

        //pushing done within the method, popping dependent on the boolean parameter
        cow = createAnimalConfigSection(builder, "Cow", "cow", "cows", true);
        chicken = createAnimalConfigSection(builder, "Chicken", "chicken", "chickens", true);
        pig = createAnimalConfigSection(builder, "Pig", "pig", "pigs", true);
        sheep = createAnimalConfigSection(builder, "Sheep", "sheep", "sheep", false);

        removeSheepEatBlock = builder
                .comment("Remove the sheep's eat block AI task. This causes sheep to no longer eat grass, and thus be unable to regenerate their wool.")
                .define("remove_eat_block", false);

        builder.pop(); //pop the sheep section

        builder.pop();
    }

    private AnimalConfigSection createAnimalConfigSection(ForgeConfigSpec.Builder builder, String categoryComment, String singular, String plural, boolean shouldPop)
    {
        builder.comment(categoryComment).push(singular);

        AnimalConfigSection animalConfig = new AnimalConfigSection(
                builder.comment(String.format("Remove the %s's float AI task. This causes %s to no longer swim in water.", singular, plural))
                .define("remove_float", false),

                builder.comment(String.format("Remove the %s's panic AI task. This causes %s to no longer run around after being hit, or search water to extinguish themselves.", singular, plural))
                .define("remove_panic", false),

                builder.comment(String.format("Remove the %s's breed AI task. This causes %s to be unable to breed to create offspring.", singular, plural))
                .define("remove_breed", false),

                builder.comment(String.format("Remove the %s's tempt AI task. This causes %s to no longer follow the player if they're holding an item they like.", singular, plural))
                .define("remove_tempt", false),

                builder.comment(String.format("Remove the %s's follow parent AI task. This causes baby %s to no longer follow their parents.", singular, plural))
                .define("remove_follow_parent", false),

                builder.comment(String.format("Remove the %s's random stroll AI task. This causes %s to no longer walk around randomly.", singular, plural))
                .define("remove_stroll", false));

        if(shouldPop)
            builder.pop();

        return animalConfig;
    }

    private FilteredConfigValue createFilteredConfigValue(ForgeConfigSpec.Builder builder, String section, String comment, String path, boolean defaultValue) {
        FilteredConfigValue filteredConfigValue;

        builder.comment(section).push(path);

        filteredConfigValue = new FilteredConfigValue(
                builder.comment(comment)
                .define(path, defaultValue),

                builder.comment("Set this to true to apply this setting to all mobs on the filter list. Set this to false to NOT apply this to mobs on the filter list.")
                .define("is_allowlist", false),

                builder.comment("The list of mobs that is affected by this setting according to is_allowlist")
                .defineList("filter_list", List.of(), e -> e instanceof String));

        builder.pop();
        return filteredConfigValue;
    }
}
