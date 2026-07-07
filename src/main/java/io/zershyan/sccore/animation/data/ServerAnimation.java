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

    public static final Codec<ServerAnimation> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("animationLocation").forGetter(Animation::animationLocation),
            Codec.STRING.optionalFieldOf("name").forGetter(Animation::name),
            Codec.INT.fieldOf("priority").forGetter(Animation::priority),
            RideData.CODEC.optionalFieldOf("rideData").forGetter(Animation::rideData),
            Codec.BOOL.fieldOf("defaultThirdPerson").forGetter(Animation::defaultThirdPerson),
            Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, Object::toString), Vec3.CODEC.listOf(2, 2).xmap(
                            vec3s -> new AABB(vec3s.getFirst(), vec3s.getLast()),
                            ab -> List.of(new Vec3(ab.minX, ab.minY, ab.minZ), new Vec3(ab.maxX, ab.maxY, ab.maxZ))
                    )).xmap(TreeMap::new, Function.identity())
                    .fieldOf("aabbMovement").forGetter(Animation::aabbMovement),
            Codec.FLOAT.fieldOf("jumpModifier").forGetter(ServerAnimation::jumpModifier)

    ).apply(i, ServerAnimation::new));
    public static final Codec<ServerAnimation> SUB_CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("animationLocation").forGetter(ServerAnimation::animationLocation),
            Codec.STRING.optionalFieldOf("name").forGetter(ServerAnimation::name),
            Codec.INT.fieldOf("priority").forGetter(ServerAnimation::priority),
            RideData.CODEC.optionalFieldOf("rideData").forGetter(ServerAnimation::rideData),
            Codec.BOOL.fieldOf("defaultThirdPerson").forGetter(ServerAnimation::defaultThirdPerson),
            Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, Object::toString), Vec3.CODEC.listOf(2, 2).xmap(
                            vec3s -> new AABB(vec3s.getFirst(), vec3s.getLast()),
                            ab -> List.of(new Vec3(ab.minX, ab.minY, ab.minZ), new Vec3(ab.maxX, ab.maxY, ab.maxZ))
                    )).xmap(TreeMap::new, Function.identity())
                    .fieldOf("aabbMovement").forGetter(Animation::aabbMovement)
    ).apply(i, ServerAnimation::new));
    public ServerAnimation(ResourceLocation animationLocation, Optional<String> name, int priority, Optional<RideData> data, boolean defaultThirdPerson, TreeMap<Integer, AABB> aabbMovement, float jumpModifier) {
        super(animationLocation, name, priority, data, defaultThirdPerson, aabbMovement);
        this.jumpModifier = jumpModifier;
    }
    public ServerAnimation(ResourceLocation animationLocation, Optional<String> name, int priority, Optional<RideData> data, boolean defaultThirdPerson, TreeMap<Integer, AABB> aabbMovement) {
        this(animationLocation, name, priority, data, defaultThirdPerson, aabbMovement, 1.0f);
    }

    public float jumpModifier() {
        return jumpModifier;
    }

    public @Nullable String getName() {
        return name.orElse(null);
    }

    public @Nullable RideData getRideData() {
        return rideData.orElse(null);
    }
}
