package io.zershyan.sccore.animation.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 欧拉角，用于表示相机变换中的旋转。
 *
 * @param pitch 俯仰角
 * @param yaw   偏航角
 * @param roll  翻滚角
 */
public record EulerAngle(float pitch, float yaw, float roll) {
    public static EulerAngle ZERO = new EulerAngle(0, 0, 0);
    public static final Codec<EulerAngle> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.fieldOf("pitch").forGetter(EulerAngle::pitch),
            Codec.FLOAT.fieldOf("yaw").forGetter(EulerAngle::yaw),
            Codec.FLOAT.fieldOf("roll").forGetter(EulerAngle::roll)
    ).apply(i, EulerAngle::new));

    /** 返回各分量相加后的新欧拉角。 */
    public EulerAngle add(float pitch, float yaw, float roll) {
        return new EulerAngle(this.pitch + pitch, this.yaw + yaw, this.roll + roll);
    }
}
