package io.zershyan.sccore.animation.api.data;

import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.core.ServerAnimationRegistry;
import io.zershyan.sccore.animation.core.SyncAnimationFactory;
import io.zershyan.sccore.animation.registry.attachment.PlayerAnimations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 动画数据操作助手类。
 * <p>
 * 提供统一的接口来操作玩家的动画数据，支持服务端和客户端两种环境。
 * 通过 {@link Opera} 内部类实现链式调用，便于批量修改动画数据。
 */
public class AnimationHelper {
    private final IAnimationService service;

    /**
     * 构造函数。
     *
     * @param service 动画服务实例
     */
    private AnimationHelper(IAnimationService service) {
        this.service = service;
    }

    /**
     * 根据玩家类型创建动画助手实例。
     * <p>
     * 服务端玩家使用 {@link AnimationService}，客户端玩家使用 {@link ClientAnimationService}。
     *
     * @param player 目标玩家
     * @return 动画助手实例
     */
    public static AnimationHelper of(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return new AnimationHelper(new AnimationService(serverPlayer));
        } else {
            return new AnimationHelper(new ClientAnimationService(player));
        }
    }

    /**
     * 使用操作对象批量修改动画数据。
     *
     * @param opera 操作函数，接收 {@link Opera} 实例并返回 {@link AnimationHelper}
     */
    public void operaData(Function<Opera, AnimationHelper> opera) {
        opera.apply(new Opera(this));
    }

    /**
     * 播放指定层的动画。
     * <p>
     * 根据动画类型自动判断是服务端动画还是客户端动画。
     *
     * @param layer       动画层的资源位置
     * @param animationId 动画的资源位置 ID
     */
    public void playAnimation(ResourceLocation layer, ResourceLocation animationId) {
        try {
            if(!SCCAnimationApi.isLayerExist(layer)) throw new RuntimeException("Unknown layer.");
            operaData(opera -> {
                if (ServerAnimationRegistry.getAnimations().containsKey(animationId)
                        || SyncAnimationFactory.getAnimations().containsKey(animationId)) {
                    opera.newServerAnim(layer, animationId);
                } else opera.newClientAnim(layer, animationId);
                return opera.endOpera();
            });
        } catch (Exception e) {
            SCCore.log.warn("Play animation error, layer : {}, animation : {}", layer, animationId);
        }
    }

    /**
     * 移除指定层的动画。
     *
     * @param layer     动画层的资源位置
     * @param isClient  是否为客户端动画
     */
    public void removeAnimation(ResourceLocation layer, boolean isClient) {
        if(SCCAnimationApi.isLayerExist(layer)) {
            operaData(opera -> {
                if (isClient) opera.removeClientAnim(layer);
                else opera.removeServerAnim(layer);
                return opera.endOpera();
            });
        } else warnUnknowLayer(layer);
    }

    /**
     * 移除指定层的所有动画（包括客户端和服务端）。
     *
     * @param layer 动画层的资源位置
     */
    public void removeAnimation(ResourceLocation layer) {
        if(SCCAnimationApi.isLayerExist(layer)) {
            operaData(opera -> opera
                    .removeClientAnim(layer)
                    .removeServerAnim(layer)
                    .endOpera()
            );
        } else warnUnknowLayer(layer);
    }

    /**
     * 服务端侧骑乘实体时该方法会被调用
     * <br>
     * 在服务端侧调用只会令播放动画
     * <br>
     * 在客户端侧调用会发包到服务端侧骑乘实体
     * @param rideAnim 骑乘动画数据
     */
    public void playRideAnimation(PlayerAnimations.RideAnim rideAnim) {
        try {
            ResourceLocation layer = rideAnim.layer().orElseThrow();
            if(!SCCAnimationApi.isLayerExist(layer)) throw new RuntimeException("Unknown layer.");
            operaData(opera -> opera.setRideAnim(layer, rideAnim.animation().orElseThrow()).endOpera());
        } catch (Exception e) {
            SCCore.log.warn("Play ride animation error, layer : {}, animation : {}",
                    rideAnim.layer().orElse(null), rideAnim.animation().orElse(null)
            );
        }
    }

    /**
     * 服务端侧停止骑乘实体时该方法会被调用
     * <br>
     * 服务端侧调用停止动画
     * <br>
     * 在客户端侧调用会发包到服务端侧停止骑乘实体
     */
    public void removeRideAnimation() {
        operaData(opera -> opera.clearRideAnim().endOpera());
    }

    /**
     * 记录未知动画层的警告日志。
     *
     * @param layer 动画层的资源位置
     */
    private void warnUnknowLayer(ResourceLocation layer) {
        SCCore.log.warn("Remove animation error, layer : {}.", layer);
    }

    /**
     * 获取玩家的动画数据。
     *
     * @return 玩家动画数据对象
     */
    public PlayerAnimations getData() {
        return service.getData();
    }

    /**
     * 动画数据操作类，支持链式调用修改动画数据。
     * <p>
     * 通过此类可以批量修改玩家的动画数据，包括客户端动画、服务端动画和骑乘动画。
     */
    public static class Opera {
        private final AnimationHelper controller;
        private PlayerAnimations data;

        /**
         * 构造函数。
         *
         * @param controller 动画助手实例
         */
        private Opera(AnimationHelper controller) {
            this.controller = controller;
            PlayerAnimations originalData = controller.service.getData();
            this.data = new PlayerAnimations(originalData.rideAnim(), originalData.clientAnimMap(), originalData.serverAnimMap());
        }

        /**
         * 修改客户端动画映射。
         *
         * @param operator 映射修改器
         * @return 当前 Opera 实例，支持链式调用
         */
        public Opera modifyClientAnimMap(Consumer<HashMap<ResourceLocation, ResourceLocation>> operator) {
            HashMap<ResourceLocation, ResourceLocation> originalAnimMap = controller.service.getData().clientAnimMap();
            HashMap<ResourceLocation, ResourceLocation> map = new HashMap<>(originalAnimMap);
            operator.accept(map);
            this.data = new PlayerAnimations(data.rideAnim(), map, data.serverAnimMap());
            return this;
        }

        /**
         * 修改服务端动画映射。
         *
         * @param operator 映射修改器
         * @return 当前 Opera 实例，支持链式调用
         */
        public Opera modifyServerAnimMap(Consumer<HashMap<ResourceLocation, ResourceLocation>> operator) {
            HashMap<ResourceLocation, ResourceLocation> originalAnimMap = controller.service.getData().serverAnimMap();
            HashMap<ResourceLocation, ResourceLocation> map = new HashMap<>(originalAnimMap);
            operator.accept(map);
            this.data = new PlayerAnimations(data.rideAnim(), data.clientAnimMap(), map);
            return this;
        }

        /**
         * 设置新的客户端动画映射。
         *
         * @param newMap 新的动画映射
         * @return 当前 Opera 实例，支持链式调用
         */
        public Opera newClientAnimMap(HashMap<ResourceLocation, ResourceLocation> newMap) {
            this.data = new PlayerAnimations(data.rideAnim(), newMap, data.serverAnimMap());
            return this;
        }

        /**
         * 设置新的服务端动画映射。
         *
         * @param newMap 新的动画映射
         * @return 当前 Opera 实例，支持链式调用
         */
        public Opera newServerAnimMap(HashMap<ResourceLocation, ResourceLocation> newMap) {
            this.data = new PlayerAnimations(data.rideAnim(), data.clientAnimMap(), newMap);
            return this;
        }

        /**
         * 添加客户端动画。
         *
         * @param layer     动画层
         * @param animation 动画资源位置
         * @return 当前 Opera 实例，支持链式调用
         */
        public Opera newClientAnim(ResourceLocation layer, ResourceLocation animation) {
            return modifyClientAnimMap(map -> map.put(layer, animation));
        }

        /**
         * 添加服务端动画。
         *
         * @param layer     动画层
         * @param animation 动画资源位置
         * @return 当前 Opera 实例，支持链式调用
         */
        public Opera newServerAnim(ResourceLocation layer, ResourceLocation animation) {
            return modifyServerAnimMap(map -> map.put(layer, animation));
        }

        /**
         * 移除客户端动画。
         *
         * @param layer 动画层
         * @return 当前 Opera 实例，支持链式调用
         */
        public Opera removeClientAnim(ResourceLocation layer) {
            return modifyClientAnimMap(map -> map.remove(layer));
        }

        /**
         * 移除服务端动画。
         *
         * @param layer 动画层
         * @return 当前 Opera 实例，支持链式调用
         */
        public Opera removeServerAnim(ResourceLocation layer) {
            return modifyServerAnimMap(map -> map.remove(layer));
        }

        /**
         * 设置骑乘动画。
         *
         * @param layer     动画层
         * @param animation 动画资源位置
         * @return 当前 Opera 实例，支持链式调用
         */
        public Opera setRideAnim(@NotNull ResourceLocation layer, @NotNull ResourceLocation animation) {
            PlayerAnimations.RideAnim rideAnim = new PlayerAnimations.RideAnim(Optional.of(layer), Optional.of(animation));
            this.data = new PlayerAnimations(rideAnim, data.clientAnimMap(), data.serverAnimMap());
            return this;
        }

        /**
         * 清除骑乘动画。
         *
         * @return 当前 Opera 实例，支持链式调用
         */
        public Opera clearRideAnim() {
            this.data = new PlayerAnimations(new PlayerAnimations.RideAnim(), data.clientAnimMap(), data.serverAnimMap());
            return this;
        }

        /**
         * 结束数据修改并触发数据同步。
         * <p>
         * 调用此方法后，所有修改的动画数据将被保存并自动同步。
         *
         * @return 动画助手实例
         */
        public AnimationHelper endOpera() {
            controller.service.setData(data);
            return controller;
        }
    }
}