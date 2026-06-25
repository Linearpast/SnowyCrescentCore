package io.zershyan.sccore.animation.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

public class ServerAnimation extends Animation {
    private final float jumpModifier;
    private final TreeMap<Integer, AABBData> aabbMovement;
    public static final Codec<ServerAnimation> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("animationLocation").forGetter(ServerAnimation::animationLocation),
            Codec.STRING.optionalFieldOf("name").forGetter(ServerAnimation::name),
            Codec.INT.fieldOf("priority").forGetter(ServerAnimation::priority),
            RideData.CODEC.optionalFieldOf("rideData").forGetter(ServerAnimation::rideData),
            Codec.BOOL.fieldOf("defaultThirdPerson").forGetter(ServerAnimation::defaultThirdPerson),
            Codec.FLOAT.fieldOf("jumpModifier").forGetter(ServerAnimation::jumpModifier),
            Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, Object::toString), AABBData.CODEC).xmap(TreeMap::new, Function.identity())
                    .fieldOf("aabbMovement").forGetter(ServerAnimation::aabbMovement)
    ).apply(i, ServerAnimation::new));
    public static final Codec<ServerAnimation> SUB_CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("animationLocation").forGetter(ServerAnimation::animationLocation),
            Codec.STRING.optionalFieldOf("name").forGetter(ServerAnimation::name),
            Codec.INT.fieldOf("priority").forGetter(ServerAnimation::priority),
            RideData.CODEC.optionalFieldOf("rideData").forGetter(ServerAnimation::rideData),
            Codec.BOOL.fieldOf("defaultThirdPerson").forGetter(ServerAnimation::defaultThirdPerson)
    ).apply(i, ServerAnimation::new));
    public ServerAnimation(ResourceLocation animationLocation, Optional<String> name, int priority, Optional<RideData> data, boolean defaultThirdPerson, float jumpModifier, TreeMap<Integer, AABBData> aabbMovement) {
        super(animationLocation, name, priority, data, defaultThirdPerson);
        this.jumpModifier = jumpModifier;
        this.aabbMovement = aabbMovement;
    }
    public ServerAnimation(ResourceLocation animationLocation, Optional<String> name, int priority, Optional<RideData> data, boolean defaultThirdPerson) {
        this(animationLocation, name, priority, data, defaultThirdPerson, 1.0f, new TreeMap<>());
    }

    public float jumpModifier() {
        return jumpModifier;
    }

    public TreeMap<Integer, AABBData> aabbMovement() {
        return aabbMovement;
    }

    public @Nullable String getName() {
        return name.orElse(null);
    }

    public @Nullable RideData getRideData() {
        return rideData.orElse(null);
    }

    public record AABBData(int tick, AABB aabb) {
        public static final Codec<AABBData> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("tick").forGetter(AABBData::tick),
                Vec3.CODEC.listOf(2, 2).xmap(
                        vec3s -> new AABB(vec3s.getFirst(), vec3s.getLast()),
                        ab -> List.of(new Vec3(ab.minX, ab.minY, ab.minZ), new Vec3(ab.maxX, ab.maxY, ab.maxZ))
                ).fieldOf("aabb").forGetter(AABBData::aabb)
        ).apply(i, AABBData::new));
    }
}
