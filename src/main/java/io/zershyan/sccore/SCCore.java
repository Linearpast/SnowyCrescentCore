package io.zershyan.sccore;


import com.mojang.logging.LogUtils;
import io.zershyan.sccore.animation.SCCoreAnimation;
import io.zershyan.sccore.common.configs.StartupConfig;
import io.zershyan.sccore.common.registry.SCCCommands;
import io.zershyan.sccore.common.registry.SCCConfigs;
import io.zershyan.sccore.example.patchouli.SCCPatchouli;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

public class SCCore {
    public static final Logger log = LogUtils.getLogger();
    public static final String MODID = "sccore";

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @Mod(SCCore.MODID)
    public static class Common {
        public Common(IEventBus modEventBus, ModContainer modContainer) {
            IEventBus neoEventBus = NeoForge.EVENT_BUS;
            SCCCommands.registerCommands(neoEventBus, modEventBus);
            SCCConfigs.register(modContainer);

            SCCoreAnimation.register(modEventBus);

            if(!FMLEnvironment.production && StartupConfig.enableExample.get()) {
                SCCPatchouli.register(neoEventBus, modEventBus);
            }
        }
    }

    @Mod(value = SCCore.MODID, dist = Dist.CLIENT)
    public static class Client {
        public Client(IEventBus modEventBus, ModContainer modContainer) {
            SCCConfigs.registerClient(modContainer);
        }
    }
}
