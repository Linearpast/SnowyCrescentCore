package io.zershyan.sccore.animation.network.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.data.ClientRideAnimDTO;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record UpdateRideAnimationData(Optional<ResourceLocation> layerLoc, Optional<Either<ResourceLocation, ClientRideAnimDTO>> animation) implements CustomPacketPayload {
    public static final Type<@NotNull UpdateRideAnimationData> TYPE =
            new Type<>(SCCore.id("animator_ride_animation"));

    public static final StreamCodec<ByteBuf, UpdateRideAnimationData> STREAM_CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.optionalFieldOf("layerLoc").forGetter(UpdateRideAnimationData::layerLoc),
            Codec.xor(ResourceLocation.CODEC, ClientRideAnimDTO.CODEC).optionalFieldOf("animation").forGetter(UpdateRideAnimationData::animation)
    ).apply(i, UpdateRideAnimationData::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
