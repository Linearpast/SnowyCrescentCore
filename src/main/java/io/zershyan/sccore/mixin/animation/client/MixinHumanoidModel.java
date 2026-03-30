package io.zershyan.sccore.mixin.animation.client;

import io.zershyan.sccore.animation.data.AnimationData;
import io.zershyan.sccore.animation.utils.AnimationUtils;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V")
    )
    private void modifyHeadRot(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci){
        if(pEntity instanceof AbstractClientPlayer player){
            AnimationData sideData = AnimationUtils.getLyingViewDefaultSide(player, null);
            if(sideData != null) {
                AnimationData.LyingType lyingType = sideData.getLyingType();
                if(lyingType != null) {
                    float pitch = pHeadPitch - 90.0f;
                    float yaw = pNetHeadYaw * -1.0f;
                    switch (lyingType){
                        case LEFT: {
                            pitch *= -1.0f;
                            yaw *= -1.0f;
                        }
                        case RIGHT: {
                            this.head.yRot = pitch * 0.017453292F;
                            this.head.xRot = yaw * 0.017453292F;
                        }
                    }
                }
            }
            AnimationData backData = AnimationUtils.getLyingViewDefaultSide(player, AnimationData.LyingType.BACK);
            if(backData != null) {
                float pitch = Mth.clamp(backData.getCamPitch() % 360.0f, -180.0f, 180.0f);
                this.head.yRot = pNetHeadYaw * 0.017453292F;
                this.head.xRot = (pHeadPitch - 90.0f - pitch) * 0.017453292F;
            }
        }
    }
}
