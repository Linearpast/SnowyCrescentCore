package com.linearpast.sccore.capability.data.entity;

import com.linearpast.sccore.SnowyCrescentCore;
import com.linearpast.sccore.capability.data.ICapabilitySync;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = SnowyCrescentCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityCapabilityHandler {
    private static final Logger log = LoggerFactory.getLogger(EntityCapabilityHandler.class);
    private static boolean isRegistered = false;

    /**
     * 应在Forge主线中调用以监听capability注册 <br>
     * 建议在Mod构造方法里调用
     */
    public static void register(IEventBus forgeBus) {
        if (isRegistered) return;
        //remainder
        forgeBus.addListener(EventPriority.HIGHEST, EntityCapabilityRemainder::capabilitySync);
        forgeBus.addListener(EventPriority.HIGHEST, EntityCapabilityRemainder::onEntityBeTracked);
        forgeBus.addListener(EventPriority.HIGHEST, EntityCapabilityRemainder::onEntityJoin);
        isRegistered = true;
    }

    //注册 capability
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerCapability(RegisterCapabilitiesEvent event) {
        EntityCapabilityRegistry.getCapabilityMap().values().forEach(record ->
                event.register(record.interfaceClass())
        );
    }

    //附加 capability
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void attachCapability(AttachCapabilitiesEvent<?> event) {
        if(event.getObject() instanceof Entity entity) {
            EntityCapabilityRegistry.getCapabilityMap().forEach((key, record) -> {
                if(record.target().isInstance(entity)) {
                    try {
                        ICapabilitySync capabilitySync = (ICapabilitySync) record.aClass().getDeclaredConstructor().newInstance();
                        event.addCapability(key, new EntityCapabilityProvider<>(key, capabilitySync));
                    } catch (Exception e) {
                        log.error("Failed to instantiate capability sync class {}. Your capability register is wrong.", record.aClass(), e);
                    }
                }
            });
        }
    }
}
