package io.zershyan.sccore.animation.network.data;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.zershyan.sccore.SCCore;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Function;

public record UpdateAnimationData(HashMap<ResourceLocation, ResourceLocation> animations, boolean isServer) implements CustomPacketPayload {
    public static final Type<@NotNull UpdateAnimationData> TYPE =
            new Type<>(SCCore.id("animator_animation"));

    public static final StreamCodec<ByteBuf, UpdateAnimationData> STREAM_CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).xmap(HashMap::new, Function.identity())
                    .fieldOf("animations").forGetter(UpdateAnimationData::animations),
            Codec.BOOL.fieldOf("isServer").forGetter(UpdateAnimationData::isServer)
    ).apply(i, UpdateAnimationData::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
