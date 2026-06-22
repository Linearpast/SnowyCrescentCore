package io.zershyan.sccore.animation.api.application;

import io.zershyan.sccore.animation.registry.AnimationAttachments;
import io.zershyan.sccore.animation.registry.attachment.AnimationData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class AnimationService implements IAnimationService {
    private final ServerPlayer player;

    protected AnimationService(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public AnimationData getData() {
        return player.getData(type());
    }

    @Override
    public void setData(AnimationData data) {
        player.setData(type(), data);
    }

    private static Supplier<AttachmentType<AnimationData>> type() {
        return AnimationAttachments.ANIMATION_DATA;
    }
}
