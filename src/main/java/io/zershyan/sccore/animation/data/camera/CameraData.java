package io.zershyan.sccore.animation.data.camera;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;

/**
 * 单帧相机数据，包含位置偏移与欧拉角。
 */
public record CameraData(Vec2 relativeOffset, Vec3 offset, EulerAngle eulerAngle) {
    public static final CameraData ZERO = new CameraData(Vec2.ZERO, Vec3.ZERO, EulerAngle.ZERO);
    public static final Codec<CameraData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Vec2.CODEC.fieldOf("relativeOffset").forGetter(CameraData::relativeOffset),
            Vec3.CODEC.fieldOf("offset").forGetter(CameraData::offset),
            EulerAngle.CODEC.fieldOf("eulerAngle").forGetter(CameraData::eulerAngle)
    ).apply(i, CameraData::new));

    public static CameraData ofRelative(Vec2 relativeOffset, EulerAngle camEulerAngles) {
        return new CameraData(relativeOffset, Vec3.ZERO, camEulerAngles);
    }

    public static CameraData of(Vec3 offset, EulerAngle camEulerAngles) {
        return new CameraData(Vec2.ZERO, offset, camEulerAngles);
    }

    public static CameraData of(Vec2 relativeOffset, Vec3 offset, EulerAngle camEulerAngles) {
        return new CameraData(relativeOffset, offset, camEulerAngles);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof CameraData(Vec2 relativeOffset1, Vec3 offset1, EulerAngle eulerAngle1))) return false;
        return this.relativeOffset.equals(relativeOffset1) && this.offset.equals(offset1) && this.eulerAngle.equals(eulerAngle1);
    }
}
