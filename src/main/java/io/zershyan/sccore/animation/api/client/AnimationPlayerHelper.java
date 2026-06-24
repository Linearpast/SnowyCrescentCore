package io.zershyan.sccore.animation.api.client;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.core.ClientAnimationRegistry;
import io.zershyan.sccore.animation.registry.attachment.PlayerAnimations;
import io.zershyan.sccore.common.datagen.init.SCCTranslatableLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * 客户端动画播放器助手类。
 * <p>
 * 提供在客户端播放和管理玩家动画的便捷方法，包括播放动画、移除动画以及更新动画状态。
 */
public class AnimationPlayerHelper {
    private final AbstractClientPlayer player;

    /**
     * 构造函数。
     *
     * @param player 目标客户端玩家
     */
    private AnimationPlayerHelper(AbstractClientPlayer player) {
        this.player = player;
    }

    /**
     * 创建动画播放器助手实例。
     *
     * @param player 目标客户端玩家
     * @return 动画播放器助手实例
     */
    public static AnimationPlayerHelper of(@NotNull AbstractClientPlayer player) {
        return new AnimationPlayerHelper(player);
    }

    /**
     * 播放指定层的动画。
     *
     * @param layer       动画层的资源位置
     * @param animationId 动画的资源位置 ID
     */
    public void playAnimation(ResourceLocation layer, ResourceLocation animationId) {
        innerPlayAnimation(layer, animationId);
    }

    /**
     * 移除指定层的动画。
     *
     * @param layer 动画层的资源位置
     */
    public void removeAnimation(ResourceLocation layer) {
        innerPlayAnimation(layer, null);
    }

    @SuppressWarnings("unchecked")
    private void innerPlayAnimation(ResourceLocation layer, @Nullable ResourceLocation animationId) {
        try {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            ModifierLayer<IAnimation> modifierLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                    .getPlayerAssociatedData(player).get(layer);
            if(modifierLayer == null) return;
            if(animationId == null) {
                modifierLayer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(
                        3, Ease.INOUTSINE), null);
                return;
            }
            KeyframeAnimation keyframeAnimation = ClientAnimationRegistry.getKeyframeAnimation(animationId);
            if(keyframeAnimation == null) {
                if(localPlayer == null) return;
                localPlayer.sendSystemMessage(Component.translatable(
                        SCCTranslatableLang.ANIMATION_RESOURCE_NOT_FOUND.getKey(),
                        animationId.toString()
                ).withStyle(ChatFormatting.RED));
                modifierLayer.replaceAnimationWithFade(
                        AbstractFadeModifier.standardFadeIn(3, Ease.INOUTSINE),
                        null
                );
                SCCAnimationApi.animation(player).operaData(opera -> opera
                        .removeClientAnim(layer)
                        .removeServerAnim(layer)
                        .endOpera()
                );
                return;
            }
            modifierLayer.replaceAnimationWithFade(
                    AbstractFadeModifier.standardFadeIn(3, Ease.INOUTSINE),
                    new KeyframeAnimationPlayer(keyframeAnimation)
            );
        }catch (Exception e) {
            SCCore.log.error("Failed to play animation : {}", animationId, e);
        }
    }

    /**
     * 更新动画状态，对比新旧动画数据并执行相应操作。
     * <p>
     * 新增或变化的动画会被播放，移除的动画会被停止。
     *
     * @param newAnimations 新的动画数据
     */
    public void updateAnimation(PlayerAnimations newAnimations) {
        PlayerAnimations oldAnimations = SCCAnimationApi.animation(player).getData();
        if(oldAnimations == null || !newAnimations.rideAnim().equals(oldAnimations.rideAnim())) {
            PlayerAnimations.RideAnim newRideAnim = newAnimations.rideAnim();
            if(oldAnimations != null) oldAnimations.rideAnim().layer().ifPresent(this::removeAnimation);
            Optional<ResourceLocation> layer = newRideAnim.layer();
            Optional<ResourceLocation> animation = newRideAnim.animation();
            if(layer.isPresent() && animation.isPresent()) playAnimation(layer.get(), animation.get());
        }
        if(oldAnimations == null || !newAnimations.clientAnimMapEqual(oldAnimations.clientAnimMap())) {
            compareAndAct(oldAnimations == null ? null : oldAnimations.clientAnimMap(), newAnimations.clientAnimMap());
        }
        if(oldAnimations == null || !newAnimations.serverAnimMapEqual(oldAnimations.serverAnimMap())) {
            compareAndAct(oldAnimations == null ? null : oldAnimations.serverAnimMap(), newAnimations.serverAnimMap());
        }
    }

    private void compareAndAct(@Nullable Map<ResourceLocation, ResourceLocation> oldMap, Map<ResourceLocation, ResourceLocation> newMap) {
        if(oldMap == null) {
            newMap.forEach(this::playAnimation);
            return;
        }

        for (Map.Entry<ResourceLocation, ResourceLocation> entry : newMap.entrySet()) {
            ResourceLocation key = entry.getKey();
            ResourceLocation newVal = entry.getValue();
            ResourceLocation oldVal = oldMap.get(key);
            if (oldVal == null || !oldVal.equals(newVal)) {
                playAnimation(key, newVal);
            }
        }

        for (Map.Entry<ResourceLocation, ResourceLocation> entry : oldMap.entrySet()) {
            ResourceLocation key = entry.getKey();
            if (!newMap.containsKey(key)) {
                removeAnimation(key);
            }
        }
    }
}