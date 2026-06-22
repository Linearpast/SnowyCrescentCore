package io.zershyan.sccore.common.configs;

import io.zershyan.sccore.common.datagen.init.SCCConfigLang;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    //invite
    public static final ModConfigSpec.ConfigValue<Integer> inviteValidTime;
    public static final ModConfigSpec.ConfigValue<Integer> inviteValidDistance;
    public static final ModConfigSpec.ConfigValue<Integer> inviteCooldown;
    //apply
    public static final ModConfigSpec.ConfigValue<Integer> applyValidTime;
    public static final ModConfigSpec.ConfigValue<Integer> applyValidDistance;
    public static final ModConfigSpec.ConfigValue<Integer> applyCooldown;
    //request
    public static final ModConfigSpec.ConfigValue<Integer> requestValidTime;
    public static final ModConfigSpec.ConfigValue<Integer> requestCooldown;

    static {
        BUILDER.push("Animation");
        //invite
        inviteValidTime = BUILDER.comment("Animation invite valid time. Ignore when zero. (seconds)")
                .translation(SCCConfigLang.inviteValidTime.getKey())
                .defineInRange(SCCConfigLang.inviteValidTime.getName(), 120, 0, Integer.MAX_VALUE);
        inviteValidDistance = BUILDER.comment("Animation invite max distance. Ignore when zero. (blocks)")
                .translation(SCCConfigLang.inviteValidDistance.getKey())
                .defineInRange(SCCConfigLang.inviteValidDistance.getName(), 6, 0, Integer.MAX_VALUE);
        inviteCooldown = BUILDER.comment("Animation invite cooldown. (seconds)")
                .translation(SCCConfigLang.inviteCooldown.getKey())
                .defineInRange(SCCConfigLang.inviteCooldown.getName(), 60, 0, Integer.MAX_VALUE);

        //apply
        applyValidTime = BUILDER.comment("Animation apply valid time. Ignore when zero. (seconds)")
                .translation(SCCConfigLang.applyValidTime.getKey())
                .defineInRange(SCCConfigLang.applyValidTime.getName(), 120, 0, Integer.MAX_VALUE);
        applyValidDistance = BUILDER.comment("Animation apply max distance. Ignore when zero. (blocks)")
                .translation(SCCConfigLang.applyValidDistance.getKey())
                .defineInRange(SCCConfigLang.applyValidDistance.getName(), 6, 0, Integer.MAX_VALUE);
        applyCooldown = BUILDER.comment("Animation apply cooldown. (seconds)")
                .translation(SCCConfigLang.applyCooldown.getKey())
                .defineInRange(SCCConfigLang.applyCooldown.getName(), 60, 0, Integer.MAX_VALUE);

        //request
        requestValidTime = BUILDER.comment("Animation request valid time. Ignore when zero (seconds)")
                .translation(SCCConfigLang.requestValidTime.getKey())
                .defineInRange(SCCConfigLang.requestValidTime.getName(), 120, 0, Integer.MAX_VALUE);
        requestCooldown = BUILDER.comment("Animation request cooldown. (seconds)")
                .translation(SCCConfigLang.requestCooldown.getKey())
                .defineInRange(SCCConfigLang.requestCooldown.getName(), 60, 0, Integer.MAX_VALUE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
