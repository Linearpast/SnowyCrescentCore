package io.zershyan.sccore.animation.registry.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.zershyan.sccore.animation.data.AABBMovement;
import io.zershyan.sccore.animation.registry.AnimationAttachments;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public record ModifyAABBData(AABBMovement aabbMovement) {
    public static final Codec<ModifyAABBData> CODEC = RecordCodecBuilder.create(i -> i.group(
        AABBMovement.CODEC.fieldOf("aabbMovementData").forGetter(ModifyAABBData::aabbMovement)
    ).apply(i, ModifyAABBData::new));
    public static final StreamCodec<ByteBuf, ModifyAABBData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static ModifyAABBData getData(Player player) {
        return player.getData(AnimationAttachments.MODIFY_AABB_DATA);
    }
}
