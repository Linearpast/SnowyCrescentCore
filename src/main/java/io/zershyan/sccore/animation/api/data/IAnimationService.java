package io.zershyan.sccore.animation.api.data;

import io.zershyan.sccore.animation.registry.attachment.PlayerAnimations;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 动画服务接口。
 * <p>
 * 定义了玩家动画数据的获取和设置操作，由服务端和客户端分别实现。
 */
public interface IAnimationService {
    /**
     * 获取玩家的动画数据。
     *
     * @return 玩家动画数据对象
     */
    PlayerAnimations getData();

    /**
     * 设置玩家的动画数据。
     *
     * @param data 玩家动画数据对象
     */
    void setData(PlayerAnimations data);

    default Optional<Map.Entry<ResourceLocation, ResourceLocation>> getHighestPriorityAnimation() {
        PlayerAnimations data = getData();
        HashMap<ResourceLocation, ResourceLocation> serverAnimMap = new HashMap<>(data.serverAnimMap());
        data.rideAnim().layer().ifPresent(layer -> serverAnimMap.put(layer, data.rideAnim().animation().orElseThrow()));
        return serverAnimMap.entrySet().stream().max(AnimationHelper.COMPARATOR);
    }
}