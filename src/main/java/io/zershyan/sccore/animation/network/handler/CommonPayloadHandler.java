package io.zershyan.sccore.animation.network.handler;

import io.zershyan.sccore.animation.handler.common.MovementAnimationTickHandler;
import io.zershyan.sccore.animation.network.data.MovementAnimationTickData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CommonPayloadHandler {
    public static void movementAnimationTick(MovementAnimationTickData data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof Player sender) {
                if(data.animationId().isEmpty()) {
                    MovementAnimationTickHandler.removeData(data.playerUUID());
                } else {
                    MovementAnimationTickHandler.putData(data.playerUUID(), data);
                }
                if(sender instanceof ServerPlayer) {
                    PacketDistributor.sendToAllPlayers(data);
                }
            }
        });
    }
}
