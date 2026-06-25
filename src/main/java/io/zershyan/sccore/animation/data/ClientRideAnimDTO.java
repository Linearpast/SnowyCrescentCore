package io.zershyan.sccore.animation.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record ClientRideAnimDTO(ResourceLocation id, ClientAnimation animation) {
    public static final Codec<ClientRideAnimDTO> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ClientRideAnimDTO::id),
            ClientAnimation.SUB_CODEC.fieldOf("animation").forGetter(ClientRideAnimDTO::animation)
    ).apply(i, ClientRideAnimDTO::new));
}
