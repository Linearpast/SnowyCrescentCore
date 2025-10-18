package com.linearpast.sccore.capability.data.player;

import com.linearpast.sccore.capability.data.ICapabilitySync;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;

import java.util.HashMap;
import java.util.Map;

public class PlayerCapabilityRegistry {
    public static final PlayerCapabilityRegistry CAPABILITIES = new PlayerCapabilityRegistry();
    private final Map<ResourceLocation, CapabilityRecord<?>> capabilityRecordMap = new HashMap<>();

    /**
     * 通过此方法注册capability，仅当 {@link net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent}
     * 事件结束之前有效
     * @param key capability的唯一name
     * @param capabilityRecord 使用record存储了应该注册的capability的各项数据，参阅：{@link CapabilityRecord}
     */
    public static void registerCapability(ResourceLocation key, CapabilityRecord<?> capabilityRecord) {
        CAPABILITIES.capabilityRecordMap.put(key, capabilityRecord);
    }

    /**
     * 通过此方法获取对应的capability数据
     * @param key 根据key获取
     * @return capability 数据
     */
    public static CapabilityRecord<?> getCapabilityRecord(ResourceLocation key){
        return CAPABILITIES.capabilityRecordMap.get(key);
    }

    public static Map<ResourceLocation, CapabilityRecord<?>> getCapabilityMap(){
        return CAPABILITIES.capabilityRecordMap;
    }

    /**
     * 记录capability的注册数据
     * @param aClass 最终会附加给玩家的实例，应该是ICapabilitySync的实例
     * @param capability 一般情况下不需要初始化它，默认：CapabilityManager.get(new CapabilityToken<>(){})
     * @param interfaceClass instance实例对应的接口类，比如ICapabilitySync.class
     */
    public record CapabilityRecord<T extends ICapabilitySync<? extends Player>>(Class<?> aClass, Capability<T> capability, Class<T> interfaceClass) {    }
}
