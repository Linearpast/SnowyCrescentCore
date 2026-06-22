package io.zershyan.sccore.common.configs;

import io.zershyan.sccore.common.datagen.init.SCCConfigLang;
import net.neoforged.neoforge.common.ModConfigSpec;

public class StartupConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue enableExample;

    static {
        BUILDER.push(SCCConfigLang.developmentConfig.getName()).translation(SCCConfigLang.developmentConfig.getKey());
        enableExample = BUILDER.translation(SCCConfigLang.enableExample.getKey())
                .define(SCCConfigLang.enableExample.getName(), true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
