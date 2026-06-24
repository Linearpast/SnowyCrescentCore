package io.zershyan.sccore.animation.network.handler;

import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.network.data.UpdateAnimationData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    public static void updateAnimMap(UpdateAnimationData data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer sender) {
                SCCAnimationApi.animation(sender).operaData(opera -> {
                    if(data.isServer()) opera.newServerAnimMap(data.animations());
                    else opera.newClientAnimMap(data.animations());
                    return opera.endOpera();
                });
            }
        });
    }
}
