package io.zershyan.sccore.animation.network.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.zershyan.sccore.SCCore;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public record MovementAnimationTickData(UUID playerUUID, Optional<ResourceLocation> animationId, int currentTick) implements CustomPacketPayload {
    public static final Type<@NotNull MovementAnimationTickData> TYPE = new Type<>(SCCore.id("movement_animation_tick"));

    public static final StreamCodec<ByteBuf, MovementAnimationTickData> STREAM_CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            UUIDUtil.CODEC.fieldOf("playerUUID").forGetter(MovementAnimationTickData::playerUUID),
            ResourceLocation.CODEC.optionalFieldOf("animationId").forGetter(MovementAnimationTickData::animationId),
            Codec.INT.fieldOf("currentTick").forGetter(MovementAnimationTickData::currentTick)
    ).apply(i, MovementAnimationTickData::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
