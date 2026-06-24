package io.zershyan.sccore.animation.api.events;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * 动画层注册事件的抽象基类。
 * <p>
 * 用于在服务端和客户端注册动画层，允许模组在初始化阶段注册自定义动画层。
 */
public abstract class LayerRegisterEvent extends Event {
    private final Map<ResourceLocation, Integer> layers = new HashMap<>();

    /**
     * 获取已注册的动画层映射。
     *
     * @return 动画层资源位置到优先级的映射
     */
    public Map<ResourceLocation, Integer> getLayers() {
        return layers;
    }

    /**
     * 注册动画层。
     *
     * @param key   动画层的资源位置
     * @param value 动画层的优先级
     */
    public void registerLayer(ResourceLocation key, Integer value) {
        layers.put(key, value);
    }

    /**
     * 客户端动画层注册事件。
     * <p>
     * 在客户端初始化时触发，用于注册客户端专属的动画层。
     */
    public static class Client extends LayerRegisterEvent { }

    /**
     * 服务端动画层注册事件。
     * <p>
     * 在服务端初始化时触发，用于注册服务端专属的动画层。
     */
    public static class Server extends LayerRegisterEvent { }
}