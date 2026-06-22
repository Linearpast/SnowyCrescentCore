package io.zershyan.sccore.animation.api.application;

import io.zershyan.sccore.animation.registry.attachment.AnimationData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class AnimationController {
    private final IAnimationService service;
    private AnimationController(IAnimationService service) {
        this.service = service;
    }

    public static AnimationController of(ServerPlayer player) {
        return new AnimationController(new AnimationService(player));
    }

    public AnimationController operaData(Function<Opera, AnimationController> operaFunc) {
        return operaFunc.apply(new Opera(this));
    }

    /**
     * 直接操作数据的方法
     */
    public static class Opera {
        private final AnimationController controller;
        private AnimationData data;
        private Opera(AnimationController controller) {
            this.controller = controller;
            AnimationData originalData = controller.service.getData();
            this.data = new AnimationData(originalData.rideAnim(), originalData.clientAnimMap(), originalData.serverAnimMap());
        }

        public Opera modifyClientAnimMap(UnaryOperator<HashMap<ResourceLocation, ResourceLocation>> operator) {
            HashMap<ResourceLocation, ResourceLocation> originalAnimMap = controller.service.getData().clientAnimMap();
            this.data = new AnimationData(data.rideAnim(), operator.apply(new HashMap<>(originalAnimMap)), data.serverAnimMap());
            return this;
        }

        public Opera modifyServerAnimMap(UnaryOperator<HashMap<ResourceLocation, ResourceLocation>> operator) {
            HashMap<ResourceLocation, ResourceLocation> originalAnimMap = controller.service.getData().serverAnimMap();
            this.data = new AnimationData(data.rideAnim(), data.clientAnimMap(), operator.apply(new HashMap<>(originalAnimMap)));
            return this;
        }

        public Opera setRideAnim(@NotNull ResourceLocation layer, @NotNull ResourceLocation animation) {
            AnimationData.RideAnim rideAnim = new AnimationData.RideAnim(Optional.of(layer), Optional.of(animation));
            this.data = new AnimationData(rideAnim, data.clientAnimMap(), data.serverAnimMap());
            return this;
        }

        public Opera clearRideAnim() {
            this.data = new AnimationData(new AnimationData.RideAnim(), data.clientAnimMap(), data.serverAnimMap());
            return this;
        }

        /**
         * 当调用这个方法时，结束数据修改，触发setData，会自动同步数据
         * @return 占位
         */
        public AnimationController endOpera() {
            controller.service.setData(data);
            return controller;
        }
    }
}
