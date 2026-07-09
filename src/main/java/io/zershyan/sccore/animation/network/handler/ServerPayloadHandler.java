package io.zershyan.sccore.animation.network.handler;

import com.mojang.datafixers.util.Either;
import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.api.server.AnimationRideHelper;
import io.zershyan.sccore.animation.data.ClientRideAnimDTO;
import io.zershyan.sccore.animation.network.data.UpdateAnimationData;
import io.zershyan.sccore.animation.network.data.UpdateRideAnimationData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

/** 服务端网络包处理器。 */
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

    public static void updateRideAnim(UpdateRideAnimationData data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer sender) {
                AnimationRideHelper helper = SCCAnimationApi.ridePlayer(sender);
                ResourceLocation layer = data.layerLoc().orElse(null);
                if(layer != null) {
                    Optional<Either<ResourceLocation, ClientRideAnimDTO>> animation = data.animation();
                    if(animation.isPresent()) {
                        Either<ResourceLocation, ClientRideAnimDTO> either = animation.get();
                        if(either.left().isPresent()) helper.startRide(layer, either.left().get());
                        else if(either.right().isPresent()) helper.startRide(layer, either.right().get());
                    } else SCCore.log.error("Has layer but animation does not exist");
                } else helper.stopRide();
            }
        });
    }
}
