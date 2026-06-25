package io.zershyan.sccore.animation.api.server;

import io.zershyan.sccore.animation.core.ServerAnimationRegistry;
import io.zershyan.sccore.animation.data.ClientRideAnimDTO;
import io.zershyan.sccore.animation.data.ServerAnimation;
import io.zershyan.sccore.animation.network.data.SyncAnimationData;
import io.zershyan.sccore.animation.registry.attachment.PlayerAnimations;
import io.zershyan.sccore.animation.registry.entity.AnimationRideEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

/**
 * 动画骑乘助手类，用于管理服务器端玩家的动画骑乘行为。
 */
public class AnimationRideHelper {
    /**
     * 关联的服务器玩家
     */
    private final ServerPlayer player;

    /**
     * 构造动画骑乘助手。
     *
     * @param player 服务器玩家
     */
    private AnimationRideHelper(ServerPlayer player) {
        this.player = player;
    }

    /**
     * 创建动画骑乘助手实例。
     *
     * @param player 服务器玩家
     * @return 动画骑乘助手实例
     */
    public static AnimationRideHelper of(ServerPlayer player) {
        return new AnimationRideHelper(player);
    }

    /**
     * 启动骑乘动画（使用客户端骑乘动画DTO）。
     *
     * @param layer     动画层
     * @param animation 客户端骑乘动画数据传输对象
     */
    public void startRide(ResourceLocation layer, ClientRideAnimDTO animation) {
        startRide(layer, new AnimationRideEntity.RideAnimation(animation.id(), animation.animation()), player.position(), false);
    }

    /**
     * 启动骑乘动画（使用资源位置）。
     *
     * @param layer   动画层
     * @param animLoc 动画资源位置
     */
    public void startRide(ResourceLocation layer, ResourceLocation animLoc) {
        startRide(layer, animLoc, player.position(), false);
    }

    /**
     * 启动骑乘动画（指定位置）。
     *
     * @param layer   动画层
     * @param animLoc 动画资源位置
     * @param pos     骑乘位置
     */
    public void startRide(ResourceLocation layer, ResourceLocation animLoc, Vec3 pos) {
        startRide(layer, animLoc, pos, false);
    }

    /**
     * 启动骑乘动画（指定位置和强制标志）。
     *
     * @param layer   动画层
     * @param animLoc 动画资源位置
     * @param pos     骑乘位置
     * @param force   是否强制启动
     */
    public void startRide(ResourceLocation layer, ResourceLocation animLoc, Vec3 pos, boolean force) {
        ServerAnimation animation = ServerAnimationRegistry.getAnimations().get(animLoc);
        startRide(layer, new AnimationRideEntity.RideAnimation(animLoc, animation), pos, force);
    }

    /**
     * 启动骑乘动画（内部方法）。
     *
     * @param layer     动画层
     * @param animation 骑乘动画
     * @param pos       骑乘位置
     * @param force     是否强制启动
     */
    private void startRide(ResourceLocation layer, AnimationRideEntity.RideAnimation animation, Vec3 pos, boolean force) {
        PlayerAnimations oldData = PlayerAnimations.getData(player);
        ResourceLocation oldLayer = oldData.rideAnim().layer().orElse(null);
        Entity vehicle = player.getVehicle();
        if (Objects.equals(layer, oldLayer) && vehicle instanceof AnimationRideEntity) {
            return;
        }
        if (layer == null) {
            stopRide();
        } else if (animation != null) {
            AnimationRideEntity.create(player, layer, animation, force, pos);
        }
    }

    /**
     * 同步骑乘动画数据给指定目标玩家。
     *
     * @param target 目标玩家
     */
    public void syncRideAnim(ServerPlayer target) {
        PacketDistributor.sendToAllPlayers(new SyncAnimationData(player.getUUID(), target.getUUID()));
    }

    /**
     * 停止当前骑乘动画。
     */
    public void stopRide() {
        if (player.getVehicle() instanceof AnimationRideEntity) {
            player.stopRiding();
        }
    }
}