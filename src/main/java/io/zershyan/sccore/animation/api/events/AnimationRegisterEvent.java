package io.zershyan.sccore.animation.api.events;

import io.zershyan.sccore.animation.api.utils.AnimationBuilder;
import io.zershyan.sccore.animation.data.ClientAnimation;
import io.zershyan.sccore.animation.data.ServerAnimation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * 动画注册事件的抽象基类。
 * <p>
 * 用于在服务端和客户端注册动画数据，允许模组在初始化阶段注册自定义动画。
 */
public abstract class AnimationRegisterEvent extends Event {
    /**
     * 客户端动画注册事件。
     * <p>
     * 在客户端初始化时触发，用于注册客户端专属的动画。
     */
    public static class Client extends AnimationRegisterEvent {
        private final Map<ResourceLocation, AnimationBuilder.Client> animations = new HashMap<>();

        /**
         * 获取已注册的客户端动画映射。
         *
         * @return 动画资源位置到客户端动画对象的映射
         */
        public Map<ResourceLocation, ClientAnimation> getAnimations() {
            Map<ResourceLocation, ClientAnimation> animationMap = new HashMap<>();
            for (Map.Entry<ResourceLocation, AnimationBuilder.Client> entry : animations.entrySet()) {
                animationMap.put(entry.getKey(), entry.getValue().build());
            }
            return animationMap;
        }

        /**
         * 注册客户端动画。
         *
         * @param location 动画的资源标识符
         * @param animationLocation 动画资源位置
         */
        public AnimationBuilder.Client createAnimation(ResourceLocation location, ResourceLocation animationLocation) {
            AnimationBuilder.Client builder = AnimationBuilder.Client.builder(animationLocation);
            animations.put(location, builder);
            return builder;
        }
    }

    /**
     * 服务端动画注册事件。
     * <p>
     * 在服务端初始化时触发，用于注册服务端专属的动画。
     */
    public static class Server extends AnimationRegisterEvent {
        private final Map<ResourceLocation, AnimationBuilder.Server> animations = new HashMap<>();

        /**
         * 获取已注册的服务端动画映射。
         *
         * @return 动画资源位置到服务端动画对象的映射
         */
        public Map<ResourceLocation, ServerAnimation> getAnimations() {
            Map<ResourceLocation, ServerAnimation> animationMap = new HashMap<>();
            for (Map.Entry<ResourceLocation, AnimationBuilder.Server> entry : animations.entrySet()) {
                animationMap.put(entry.getKey(), entry.getValue().build());
            }
            return animationMap;
        }

        /**
         * 注册服务端动画。
         *
         * @param location 动画的资源标识符
         * @param animationLocation 动画资源位置
         */
        public AnimationBuilder.Server createAnimation(ResourceLocation location, ResourceLocation animationLocation) {
            AnimationBuilder.Server builder = AnimationBuilder.Server.builder(animationLocation);
            animations.put(location, builder);
            return builder;
        }
    }
}