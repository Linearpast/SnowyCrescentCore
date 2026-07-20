package io.zershyan.sccore.animation.data.camera;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * 相机变换关键帧序列，按 tick 存储相机偏移与欧拉角，支持线性插值采样。
 */
public record CameraChange(TreeMap<Integer, CameraData> movement) {
    public CameraChange() {
        this(new TreeMap<>());
    }

    public static final Codec<CameraChange> CODEC = RecordCodecBuilder.create(i -> i.group(
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
        Vec2 relativeOffset = new Vec2(
                Mth.lerp(alpha, a.relativeOffset().x(), b.relativeOffset().x()),
                Mth.lerp(alpha, a.relativeOffset().y(), b.relativeOffset().y())
        );
        Vec3 offset = new Vec3(
                Mth.lerp(alpha, a.offset().x, b.offset().x),
                Mth.lerp(alpha, a.offset().y, b.offset().y),
                Mth.lerp(alpha, a.offset().z, b.offset().z)
        );
        EulerAngle euler = new EulerAngle(
                Mth.lerp(alpha, a.eulerAngle().pitch(), b.eulerAngle().pitch()),
                Mth.lerp(alpha, a.eulerAngle().yaw(), b.eulerAngle().yaw()),
                Mth.lerp(alpha, a.eulerAngle().roll(), b.eulerAngle().roll())
        );
        return new CameraData(relativeOffset, offset, euler);
    }
}
