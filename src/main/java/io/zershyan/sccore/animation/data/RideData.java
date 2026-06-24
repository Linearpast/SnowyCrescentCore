package io.zershyan.sccore.animation.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record RideData(List<ResourceLocation> componentAnimations, Vec3 offset, int existTick, float xRot, float yRot) {
    public static final Codec<RideData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.list(ResourceLocation.CODEC).fieldOf("componentAnimations").forGetter(RideData::componentAnimations),
            Vec3.CODEC.fieldOf("offset").forGetter(RideData::offset),
            Codec.INT.fieldOf("existTick").forGetter(RideData::existTick),
            Codec.FLOAT.fieldOf("xRot").forGetter(RideData::xRot),
            Codec.FLOAT.fieldOf("yRot").forGetter(RideData::yRot)
    ).apply(i, RideData::new));
}
