package io.zershyan.sccore.animation.api.data;

import io.zershyan.sccore.animation.registry.AnimationAttachments;
import io.zershyan.sccore.animation.registry.attachment.PlayerAnimations;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class AnimationService implements IAnimationService {
    private final ServerPlayer player;

    protected AnimationService(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public PlayerAnimations getData() {
        return PlayerAnimations.getData(player);
    }

    @Override
    public void setData(PlayerAnimations data) {
        player.setData(type(), data);
    }

    private static Supplier<AttachmentType<PlayerAnimations>> type() {
        return AnimationAttachments.PLAYER_ANIMATIONS;
    }
}
