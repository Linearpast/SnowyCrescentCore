package io.zershyan.sccore.animation.api;

import io.zershyan.sccore.animation.api.client.AnimationPlayerHelper;
import io.zershyan.sccore.animation.api.data.AnimationHelper;
import io.zershyan.sccore.animation.core.ClientAnimationRegistry;
import io.zershyan.sccore.animation.core.ServerAnimationRegistry;
import io.zershyan.sccore.compat.SCCoreCompat;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SCCAnimationApi {

    static {
        if(!SCCoreCompat.PlayerAnimator.isModLoaded()) {
            throw new RuntimeException("Use Api with Mod Player Animator Uninstalled");
        }
    }

    public static AnimationHelper animation(Player player) {
        return AnimationHelper.of(player);
    }

    @OnlyIn(Dist.CLIENT)
    public static AnimationPlayerHelper animPlayer(AbstractClientPlayer player) {
        return AnimationPlayerHelper.of(player);
    }

    public static boolean isLayerExist(ResourceLocation layer) {
        return ServerAnimationRegistry.getLayers().containsKey(layer)
                || ClientAnimationRegistry.getAllLayers().containsKey(layer);
    }

    public static boolean isAnimationExist(ResourceLocation animationId) {
        return ServerAnimationRegistry.getAnimations().containsKey(animationId)
                || ClientAnimationRegistry.getAllAnimations().containsKey(animationId);
    }
}
