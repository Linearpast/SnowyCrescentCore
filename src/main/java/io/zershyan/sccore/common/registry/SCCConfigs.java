package io.zershyan.sccore.common.registry;

import io.zershyan.sccore.common.configs.ServerConfig;
import io.zershyan.sccore.common.configs.StartupConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class SCCConfigs {
    public static void register(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.STARTUP, StartupConfig.SPEC);
    }

    public static void registerClient(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
