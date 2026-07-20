package io.zershyan.sccore.animation.handler.client;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.network.data.MovementAnimationTickData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = SCCore.MODID, value = Dist.CLIENT)
public class AnimationPlayerHandler {
    private static KeyframeAnimationPlayer currentAnimation;

    @SubscribeEvent
    public static void sendAnimationTick(ClientTickEvent.Pre event) {
        try {
            Minecraft instance = Minecraft.getInstance();
            AbstractClientPlayer player = instance.player;
            if(player == null) return;
            Optional<Map.Entry<ResourceLocation, ResourceLocation>> max = SCCAnimationApi.animation(player).getHighestPriorityAnimation(
                    animation -> !animation.aabbMovement().isEmpty()
            );
            if(max.isEmpty()) return;
            Map.Entry<ResourceLocation, ResourceLocation> entry = max.get();
            currentAnimation = SCCAnimationApi.animPlayer(player).getKeyframeAnimationPlayer(entry.getKey());
            if(currentAnimation == null) return;
            int currentTick = currentAnimation.getCurrentTick();
            MovementAnimationTickData movementAnimationTickData = new MovementAnimationTickData(player.getUUID(), Optional.of(entry.getValue()), currentTick);
            PacketDistributor.sendToServer(movementAnimationTickData);
        } catch (Exception ignored) { }
    }

    @SubscribeEvent
    public static void checkValidAnimation(ClientTickEvent.Post event) {
        try {
            Minecraft instance = Minecraft.getInstance();
            AbstractClientPlayer player = instance.player;
            if(player == null) return;
            if(currentAnimation == null) return;
            if(currentAnimation.isActive()) return;
            MovementAnimationTickData movementAnimationTickData = new MovementAnimationTickData(player.getUUID(), Optional.empty(), 0);
            PacketDistributor.sendToServer(movementAnimationTickData);
        } catch (Exception ignored) { }
    }
}
