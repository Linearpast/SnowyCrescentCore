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

public class AnimationHelper {
    private final IAnimationService service;
    private AnimationHelper(IAnimationService service) {
        this.service = service;
    }

    public static AnimationHelper of(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return new AnimationHelper(new AnimationService(serverPlayer));
        } else {
            return new AnimationHelper(new ClientAnimationService(player));
        }
    }

    public void operaData(Function<Opera, AnimationHelper> opera) {
        opera.apply(new Opera(this));
    }

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

    public void removeAnimation(ResourceLocation layer, boolean isClient) {
        if(SCCAnimationApi.isLayerExist(layer)) {
            operaData(opera -> {
                if (isClient) opera.removeClientAnim(layer);
                else opera.removeServerAnim(layer);
                return opera.endOpera();
            });
        } else warnUnknowLayer(layer);
    }

    public void removeAnimation(ResourceLocation layer) {
        if(SCCAnimationApi.isLayerExist(layer)) {
            operaData(opera -> opera
                    .removeClientAnim(layer)
                    .removeServerAnim(layer)
                    .endOpera()
            );
        } else warnUnknowLayer(layer);
    }

    private void warnUnknowLayer(ResourceLocation layer) {
        SCCore.log.warn("Remove animation error, layer : {}.", layer);
    }

    public PlayerAnimations getData() {
        return service.getData();
    }

    /**
     * 直接操作数据的方法
     */
    public static class Opera {
        private final AnimationHelper controller;
        private PlayerAnimations data;
        private Opera(AnimationHelper controller) {
            this.controller = controller;
            PlayerAnimations originalData = controller.service.getData();
            this.data = new PlayerAnimations(originalData.rideAnim(), originalData.clientAnimMap(), originalData.serverAnimMap());
        }

        public Opera modifyClientAnimMap(Consumer<HashMap<ResourceLocation, ResourceLocation>> operator) {
            HashMap<ResourceLocation, ResourceLocation> originalAnimMap = controller.service.getData().clientAnimMap();
            HashMap<ResourceLocation, ResourceLocation> map = new HashMap<>(originalAnimMap);
            operator.accept(map);
            this.data = new PlayerAnimations(data.rideAnim(), map, data.serverAnimMap());
            return this;
        }

        public Opera modifyServerAnimMap(Consumer<HashMap<ResourceLocation, ResourceLocation>> operator) {
            HashMap<ResourceLocation, ResourceLocation> originalAnimMap = controller.service.getData().serverAnimMap();
            HashMap<ResourceLocation, ResourceLocation> map = new HashMap<>(originalAnimMap);
            operator.accept(map);
            this.data = new PlayerAnimations(data.rideAnim(), data.clientAnimMap(), map);
            return this;
        }

        public Opera newClientAnimMap(HashMap<ResourceLocation, ResourceLocation> newMap) {
            this.data = new PlayerAnimations(data.rideAnim(), newMap, data.serverAnimMap());
            return this;
        }

        public Opera newServerAnimMap(HashMap<ResourceLocation, ResourceLocation> newMap) {
            this.data = new PlayerAnimations(data.rideAnim(), data.clientAnimMap(), newMap);
            return this;
        }

        public Opera newClientAnim(ResourceLocation layer, ResourceLocation animation) {
            return modifyClientAnimMap(map -> map.put(layer, animation));
        }

        public Opera newServerAnim(ResourceLocation layer, ResourceLocation animation) {
            return modifyServerAnimMap(map -> map.put(layer, animation));
        }

        public Opera removeClientAnim(ResourceLocation layer) {
            return modifyClientAnimMap(map -> map.remove(layer));
        }

        public Opera removeServerAnim(ResourceLocation layer) {
            return modifyServerAnimMap(map -> map.remove(layer));
        }

        public Opera setRideAnim(@NotNull ResourceLocation layer, @NotNull ResourceLocation animation) {
            PlayerAnimations.RideAnim rideAnim = new PlayerAnimations.RideAnim(Optional.of(layer), Optional.of(animation));
            this.data = new PlayerAnimations(rideAnim, data.clientAnimMap(), data.serverAnimMap());
            return this;
        }

        public Opera clearRideAnim() {
            this.data = new PlayerAnimations(new PlayerAnimations.RideAnim(), data.clientAnimMap(), data.serverAnimMap());
            return this;
        }

        /**
         * 当调用这个方法时，结束数据修改，触发setData，会自动同步数据
         * @return 占位
         */
        public AnimationHelper endOpera() {
            controller.service.setData(data);
            return controller;
        }
    }
}
