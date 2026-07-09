package io.zershyan.sccore.animation.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * 客户端动画数据，在 {@link Animation} 基础上增加相机变换（第一人称/第三人称）。
 *
 * <p>包含 {@link CameraChange} 内部记录，定义了按 tick 插值的相机偏移与欧拉角关键帧序列，
 * 用于 {@code CameraTransformStateHandler} 在渲染时驱动相机变换。</p>
 *
 * @see Animation
 * @see ServerAnimation
 * @see CameraChange
 */
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
    public ClientAnimation(ResourceLocation animationLocation, @Nullable String name, int priority, @Nullable RideData rideData, boolean defaultThirdPerson, CameraChange firstPersonCameraChange, CameraChange cameraChange) {
        super(animationLocation, Optional.ofNullable(name), priority, Optional.ofNullable(rideData), defaultThirdPerson, new TreeMap<>());
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

    /**
     * 相机变换关键帧序列，按 tick 存储相机偏移与欧拉角，支持线性插值采样。
     */
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

        /**
         * 在 movement 关键帧之间按 tick 线性插值，得到该时刻的相机变换数据。
         *
         * @param tick 采样时刻（通常为 currentTick + partialTick）
         * @return 插值后的相机数据；movement 为空时返回 {@code null}
         */
        public CameraData sample(float tick) {
            if (movement.isEmpty()) return null;
            int floor = (int) Math.floor(tick);
            Map.Entry<Integer, CameraData> floorEntry = movement.floorEntry(floor);
            Map.Entry<Integer, CameraData> ceilEntry = movement.ceilingEntry(floor);
            if (floorEntry == null) return ceilEntry.getValue();
            if (ceilEntry == null) return floorEntry.getValue();
            if (floorEntry.getKey().equals(ceilEntry.getKey())) return floorEntry.getValue();
            float delta = ceilEntry.getKey() - floorEntry.getKey();
            float alpha = (tick - floorEntry.getKey()) / delta;
            CameraData a = floorEntry.getValue();
            CameraData b = ceilEntry.getValue();
            Vec3 offset = new Vec3(
                    Mth.lerp(alpha, a.offset().x, b.offset().x),
                    Mth.lerp(alpha, a.offset().y, b.offset().y),
                    Mth.lerp(alpha, a.offset().z, b.offset().z)
            );
            EulerAngle euler = new EulerAngle(
                    Mth.lerp(alpha, a.camEulerAngles().pitch(), b.camEulerAngles().pitch()),
                    Mth.lerp(alpha, a.camEulerAngles().yaw(), b.camEulerAngles().yaw()),
                    Mth.lerp(alpha, a.camEulerAngles().roll(), b.camEulerAngles().roll())
            );
            return new CameraData(offset, euler);
        }
    }

    /** 单帧相机数据，包含位置偏移与欧拉角。 */
    public record CameraData(Vec3 offset, EulerAngle camEulerAngles) {
        public static final Codec<CameraData> CODEC = RecordCodecBuilder.create(i -> i.group(
                Vec3.CODEC.fieldOf("offset").forGetter(CameraData::offset),
                EulerAngle.CODEC.fieldOf("camEulerAngles").forGetter(CameraData::camEulerAngles)
        ).apply(i, CameraData::new));
    }
}
