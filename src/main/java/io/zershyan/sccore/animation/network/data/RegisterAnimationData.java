package io.zershyan.sccore.animation.network.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.data.ServerAnimation;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Function;

public record RegisterAnimationData(HashMap<ResourceLocation, ServerAnimation> animations) implements CustomPacketPayload {
    public static final Type<@NotNull RegisterAnimationData> TYPE =
            new Type<>(SCCore.id("animator_animations"));

    public static final StreamCodec<ByteBuf, RegisterAnimationData> STREAM_CODEC = ByteBufCodecs.fromCodecTrusted(RecordCodecBuilder.create(
            i -> i.group(Codec.unboundedMap(ResourceLocation.CODEC, ServerAnimation.SUB_CODEC)
                    .xmap(HashMap::new, Function.identity())
                    .fieldOf("animations")
                    .forGetter(RegisterAnimationData::animations)
            ).apply(i, RegisterAnimationData::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
