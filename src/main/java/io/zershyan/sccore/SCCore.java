package io.zershyan.sccore;


import com.mojang.logging.LogUtils;
import io.zershyan.sccore.animation.handler.client.CameraTransformStateHandler;
import io.zershyan.sccore.common.configs.StartupConfig;
import io.zershyan.sccore.common.registry.SCCCommands;
import io.zershyan.sccore.common.registry.SCCConfigs;
import io.zershyan.sccore.compat.SCCoreCompat;
import io.zershyan.sccore.example.animation.ExampleAnimations;
import io.zershyan.sccore.example.patchouli.ExamplePatchouli;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
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

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @Mod(SCCore.MODID)
    public static class Common {
        public Common(IEventBus modEventBus, ModContainer modContainer) {
            IEventBus neoEventBus = NeoForge.EVENT_BUS;

            SCCConfigs.register(modContainer);
            SCCoreCompat.register(neoEventBus, modEventBus);
            SCCCommands.registerCommands(neoEventBus, modEventBus);

            if(!FMLEnvironment.production && StartupConfig.enableExample.get()) {
                ExampleAnimations.register(neoEventBus);
                ExamplePatchouli.register(neoEventBus, modEventBus);
            }
        }
    }

    @Mod(value = SCCore.MODID, dist = Dist.CLIENT)
    public static class Client {
        public Client(IEventBus modEventBus, ModContainer modContainer) {
            IEventBus neoEventBus = NeoForge.EVENT_BUS;

            SCCConfigs.registerClient(modContainer);

            if(!FMLEnvironment.production && StartupConfig.enableExample.get()) {
                ExampleAnimations.registerClient(neoEventBus);
            }
        }
    }

    public static void func(HumanoidModel<?> model, float yaw, float pitch) {
        ModelPart head = model.head;
        head.xRot = 0;
        head.yRot = 0;
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) return;
        CameraTransformStateHandler.Snapshot snapshot = CameraTransformStateHandler.get(player.getUUID(), true);

    }
}
