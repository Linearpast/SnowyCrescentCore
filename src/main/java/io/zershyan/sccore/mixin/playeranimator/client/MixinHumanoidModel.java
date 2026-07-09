package io.zershyan.sccore.mixin.playeranimator.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import io.zershyan.sccore.animation.data.EulerAngle;
import io.zershyan.sccore.animation.handler.client.CameraTransformStateHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class MixinHumanoidModel<T extends LivingEntity> extends AgeableListModel<T> implements ArmedModel, HeadedModel {

    @Shadow @Final public ModelPart head;

    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At("HEAD")
    )
    public void setup(
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci,
            @Local(name = "netHeadYaw", ordinal = 3, argsOnly = true) LocalFloatRef yaw,
            @Local(name = "headPitch", ordinal = 4, argsOnly = true) LocalFloatRef pitch
    ) {
        if (!(entity instanceof AbstractClientPlayer player)) return;
        CameraTransformStateHandler.Snapshot cur = CameraTransformStateHandler.get(player.getUUID(), true);
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        EulerAngle euler = CameraTransformStateHandler.lerpEuler(cur.old, cur, partialTick);
        if (euler.pitch() == 0f && euler.yaw() == 0f && euler.roll() == 0f) return;

        float rad = (float) Math.toRadians(-euler.roll());
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        yaw.set(netHeadYaw * cos - headPitch * sin);
        pitch.set(netHeadYaw * sin + headPitch * cos);
    }
}
