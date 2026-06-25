package io.zershyan.sccore.animation.network.handler;

import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.core.SyncAnimationFactory;
import io.zershyan.sccore.animation.data.ClientAnimation;
import io.zershyan.sccore.animation.data.ServerAnimation;
import io.zershyan.sccore.animation.network.data.RegisterAnimationData;
import io.zershyan.sccore.animation.network.data.RegisterLayerData;
import io.zershyan.sccore.animation.network.data.SyncAnimationData;
import io.zershyan.sccore.animation.network.data.TurnThirdPersonData;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public class ClientPayloadHandler {
    public static void registerLayers(RegisterLayerData data, IPayloadContext context) {
        context.enqueueWork(() -> SyncAnimationFactory.reloadLayers(data.layers()));
    }

    public static void registerAnimations(RegisterAnimationData data, IPayloadContext context) {
        context.enqueueWork(() -> {
            HashMap<ResourceLocation, ServerAnimation> animations = data.animations();
            Map<ResourceLocation, ClientAnimation> results = new HashMap<>();
            animations.forEach((location, animation) ->
                    results.put(location, new ClientAnimation(animation)));
            SyncAnimationFactory.reloadAnimations(results);
        });
    }

    public static void syncAnimation(SyncAnimationData data, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            Player player = level.getPlayerByUUID(data.player());
            Player target = level.getPlayerByUUID(data.target());
            if(player instanceof AbstractClientPlayer clientPlayer && target instanceof AbstractClientPlayer targetPlayer) {
                SCCAnimationApi.animPlayer(clientPlayer).syncAnimation(targetPlayer);
            }
        });
    }

    public static void turnThirdPerson(TurnThirdPersonData data, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK));
    }
}
