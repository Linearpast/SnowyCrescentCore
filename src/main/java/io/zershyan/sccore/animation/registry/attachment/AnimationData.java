package io.zershyan.sccore.animation.registry.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public record AnimationData(RideAnim rideAnim, HashMap<ResourceLocation, ResourceLocation> clientAnimMap, HashMap<ResourceLocation, ResourceLocation> serverAnimMap) {
    public record RideAnim(Optional<ResourceLocation> layer, Optional<ResourceLocation> animation) {
        public static final Codec<RideAnim> CODEC = RecordCodecBuilder.create(i -> i.group(
                ResourceLocation.CODEC.optionalFieldOf("layer").forGetter(RideAnim::layer),
                ResourceLocation.CODEC.optionalFieldOf("animation").forGetter(RideAnim::animation)
        ).apply(i, RideAnim::new));
        public RideAnim() {
            this(Optional.empty(), Optional.empty());
        }
    }
    public static final Codec<AnimationData> CODEC = RecordCodecBuilder.create(i -> i.group(
            RideAnim.CODEC.fieldOf("rideAnim").forGetter(AnimationData::rideAnim),
            Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).xmap(HashMap::new, Function.identity())
                    .fieldOf("clientAnimMap").forGetter(AnimationData::clientAnimMap),
            Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).xmap(HashMap::new, Function.identity())
                    .fieldOf("serverAnimMap").forGetter(AnimationData::serverAnimMap)
    ).apply(i, AnimationData::new));
    public static final StreamCodec<ByteBuf, AnimationData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
    public AnimationData() {
        this(new RideAnim(), new HashMap<>(), new HashMap<>());
    }
}
