package com.linearpast.sccore.capability.data.player;

import com.linearpast.sccore.SnowyCrescentCore;
import com.linearpast.sccore.capability.data.ICapabilitySync;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = SnowyCrescentCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerCapabilityHandler {
    private static final Logger log = LoggerFactory.getLogger(PlayerCapabilityHandler.class);
    private static boolean isRegistered = false;

    /**
     * 应在Forge主线中调用以监听capability注册 <br>
     * 建议在Mod构造方法里调用
     */
    public static void register(IEventBus forgeBus) {
        if (isRegistered) return;
        //remainder
        forgeBus.addListener(EventPriority.HIGHEST, PlayerCapabilityRemainder::capabilitySync);
        forgeBus.addListener(EventPriority.HIGHEST, PlayerCapabilityRemainder::onPlayerClone);
        forgeBus.addListener(EventPriority.HIGHEST, PlayerCapabilityRemainder::onPlayerRespawn);
        forgeBus.addListener(EventPriority.HIGHEST, PlayerCapabilityRemainder::onEntityBeTracked);
        forgeBus.addListener(EventPriority.HIGHEST, PlayerCapabilityRemainder::onPlayerLogin);
        isRegistered = true;
    }

    //注册 capability
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerCapability(RegisterCapabilitiesEvent event) {
        PlayerCapabilityRegistry.getCapabilityMap().values().forEach(record ->
                event.register(record.interfaceClass())
        );
    }

    //附加 capability
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void attachCapability(AttachCapabilitiesEvent<?>  event) {
        if(event.getObject() instanceof Player) {
            PlayerCapabilityRegistry.getCapabilityMap().forEach((key, record) -> {
                try {
                    ICapabilitySync capabilitySync = (ICapabilitySync) record.aClass().getDeclaredConstructor().newInstance();
                    event.addCapability(key, new PlayerCapabilityProvider<>(key, capabilitySync));
                } catch (Exception e) {
                    log.error("Failed to instantiate capability sync class {}. Your capability register is wrong.", record.aClass(), e);
                }
            });
        }
    }
}
