package io.zershyan.sccore.network;

import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.compat.SCCoreCompat;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = SCCore.MODID)
public class PayloadRegister {
    @NotNull
    public static final String PROTOCOL_VERSION = ModList.get()
            .getModContainerById(SCCore.MODID)
            .map(ModContainer::getModInfo)
            .map(IModInfo::getVersion)
            .map(Object::toString)
            .orElse("unknown");

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        SCCoreCompat.registerNetwork(registrar);
    }
}
