package com.linearpast.sccore.capability.data.entity;

import com.linearpast.sccore.capability.data.ICapabilitySync;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;

import java.util.HashMap;
import java.util.Map;

public class EntityCapabilityRegistry {
    public static final EntityCapabilityRegistry CAPABILITIES = new EntityCapabilityRegistry();
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

    /**
     * 获取所有key对应的cap数据集
     * @return map
     */
    public static Map<ResourceLocation, CapabilityRecord<?>> getCapabilityMap(){
        return CAPABILITIES.capabilityRecordMap;
    }

    /**
     * 记录capability的注册数据
     * @param aClass 最终会附加给实体的实例的类，应该是实现了clazz的类
     * @param capability 注册时一般默认{@code CapabilityManager.get(new CapabilityToken<>(){})}即可
     * @param interfaceClass instance类对应的实例对应的接口类，比如ICapabilitySync.class
     * @param target capability附加的目标类型
     */
    public record CapabilityRecord<T extends ICapabilitySync>(Class<?> aClass, Capability<T> capability, Class<T> interfaceClass, Class<? extends Entity> target) {

    }
}
