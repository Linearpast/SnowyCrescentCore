package io.zershyan.sccore.animation.api;

import io.zershyan.sccore.animation.api.client.AnimationPlayerHelper;
import io.zershyan.sccore.animation.api.data.AnimationHelper;
import io.zershyan.sccore.animation.api.server.AnimationRideHelper;
import io.zershyan.sccore.animation.core.ClientAnimationRegistry;
import io.zershyan.sccore.animation.core.ServerAnimationRegistry;
import io.zershyan.sccore.compat.SCCoreCompat;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * SnowyCrescentCore 动画 API 的主入口类。
 * <p>
 * 提供动画相关的便捷访问方法，包括动画数据操作、玩家动画控制等功能。
 * 使用前需确保 Player Animator 模组已安装。
 */
public class SCCAnimationApi {

    static {
        if(!SCCoreCompat.PlayerAnimator.isModLoaded()) {
            throw new RuntimeException("Use Api with Mod Player Animator Uninstalled");
        }
    }

    /**
     * 获取指定玩家的动画数据助手。
     * <p>
     * 用于操作玩家的动画数据，如播放、停止动画等。
     *
     * @param player 目标玩家实体
     * @return 动画数据助手实例
     */
    public static AnimationHelper animation(Player player) {
        return AnimationHelper.of(player);
    }

    /**
     * 获取客户端玩家的动画播放器助手。
     * <p>
     * 用于在客户端控制玩家动画的播放状态。
     *
     * @param player 客户端玩家实体
     * @return 动画播放器助手实例
     */
    @OnlyIn(Dist.CLIENT)
    public static AnimationPlayerHelper animPlayer(AbstractClientPlayer player) {
        return AnimationPlayerHelper.of(player);
    }

    public static AnimationRideHelper ridePlayer(ServerPlayer player) {
        return AnimationRideHelper.of(player);
    }

    /**
     * 检查指定的动画层是否存在。
     * <p>
     * 同时检查服务端和客户端的动画层注册表。
     *
     * @param layer 动画层的资源位置
     * @return 如果动画层存在返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isLayerExist(ResourceLocation layer) {
        return ServerAnimationRegistry.getLayers().containsKey(layer)
                || ClientAnimationRegistry.getAllLayers().containsKey(layer);
    }

    /**
     * 检查指定的动画是否存在。
     * <p>
     * 同时检查服务端和客户端的动画注册表。
     *
     * @param animationId 动画的资源位置 ID
     * @return 如果动画存在返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isAnimationExist(ResourceLocation animationId) {
        return ServerAnimationRegistry.getAnimations().containsKey(animationId)
                || ClientAnimationRegistry.getAllAnimations().containsKey(animationId);
    }
}