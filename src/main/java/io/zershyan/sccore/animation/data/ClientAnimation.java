package io.zershyan.sccore.animation.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kosmx.playerAnim.core.util.Vec3f;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

public class ClientAnimation extends Animation {
    private final CameraChange firstPersonCameraChange;
    private final CameraChange cameraChange;
    public static final Codec<ClientAnimation> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("animationLocation").forGetter(ClientAnimation::animationLocation),
            Codec.STRING.optionalFieldOf("name").forGetter(ClientAnimation::name),
            Codec.INT.fieldOf("priority").forGetter(ClientAnimation::priority),
            RideData.CODEC.optionalFieldOf("rideData").forGetter(ClientAnimation::rideData),
            Codec.BOOL.fieldOf("defaultThirdPerson").forGetter(ClientAnimation::defaultThirdPerson),
            CameraChange.CODEC.fieldOf("firstPersonCameraChange").forGetter(ClientAnimation::firstPersonCameraChange),
            CameraChange.CODEC.fieldOf("cameraChange").forGetter(ClientAnimation::cameraChange)
    ).apply(i, ClientAnimation::new));
    public static final Codec<ClientAnimation> SUB_CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("animationLocation").forGetter(ClientAnimation::animationLocation),
            Codec.STRING.optionalFieldOf("name").forGetter(ClientAnimation::name),
            Codec.INT.fieldOf("priority").forGetter(ClientAnimation::priority),
            RideData.CODEC.optionalFieldOf("rideData").forGetter(ClientAnimation::rideData),
            Codec.BOOL.fieldOf("defaultThirdPerson").forGetter(ClientAnimation::defaultThirdPerson)
    ).apply(i, ClientAnimation::new));
    public ClientAnimation(ResourceLocation animationLocation, Optional<String> name, int priority, Optional<RideData> rideData, boolean defaultThirdPerson, CameraChange firstPersonCameraChange, CameraChange cameraChange) {
        super(animationLocation, name, priority, rideData, defaultThirdPerson, new TreeMap<>());
        this.firstPersonCameraChange = firstPersonCameraChange;
        this.cameraChange = cameraChange;
    }
    public ClientAnimation(ResourceLocation animationLocation, Optional<String> name, int priority, Optional<RideData> rideData, boolean defaultThirdPerson) {
        this(animationLocation, name, priority, rideData, defaultThirdPerson, new CameraChange(true), new CameraChange(false));
    }
    public ClientAnimation(ResourceLocation animationLocation, @Nullable String name, int priority, @Nullable RideData data, boolean defaultThirdPerson) {
        this(animationLocation, Optional.ofNullable(name), priority, Optional.ofNullable(data), defaultThirdPerson, new CameraChange(true), new CameraChange(false));
    }
    public ClientAnimation(ServerAnimation animation) {
        super(animation.animationLocation(), Optional.ofNullable(animation.getName()), animation.priority(), Optional.ofNullable(animation.getRideData()), animation.defaultThirdPerson(), animation.aabbMovement());
        this.firstPersonCameraChange = new CameraChange(true);
        this.cameraChange = new CameraChange(false);
    }



    public CameraChange firstPersonCameraChange() {
        return firstPersonCameraChange;
    }
    public CameraChange cameraChange() {
        return cameraChange;
    }

    public record CameraChange(boolean relative, TreeMap<Integer, CameraData> movement) {
        public CameraChange(boolean relative) {
            this(relative, new TreeMap<>());
        }
        public static final Codec<CameraChange> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.BOOL.fieldOf("relative").forGetter(CameraChange::relative),
                Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, Object::toString), CameraData.CODEC)
                        .xmap(TreeMap::new, Function.identity())
                        .fieldOf("movement")
                        .forGetter(CameraChange::movement)
        ).apply(i, CameraChange::new));
    }
    public record CameraData(int tick, Vec3 offset, Vec3f camEulerAngles) {
        public static final Codec<CameraData> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("tick").forGetter(CameraData::tick),
                Vec3.CODEC.fieldOf("offset").forGetter(CameraData::offset),
                Codec.FLOAT.listOf(3, 3).xmap(
                        list -> new Vec3f(list.getFirst(), list.get(1), list.get(2)),
                        vec -> List.of(vec.getX(), vec.getY(), vec.getZ())
                ).fieldOf("camEulerAngles").forGetter(CameraData::camEulerAngles)
        ).apply(i, CameraData::new));
    }
}
