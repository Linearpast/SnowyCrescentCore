package io.zershyan.sccore.animation.network.handler;

import io.zershyan.sccore.animation.core.SyncAnimationFactory;
import io.zershyan.sccore.animation.data.ClientAnimation;
import io.zershyan.sccore.animation.data.ServerAnimation;
import io.zershyan.sccore.animation.network.data.RegisterAnimationData;
import io.zershyan.sccore.animation.network.data.RegisterLayerData;
import net.minecraft.resources.ResourceLocation;
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
}
