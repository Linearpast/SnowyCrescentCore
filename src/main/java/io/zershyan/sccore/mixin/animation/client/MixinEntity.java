package io.zershyan.sccore.mixin.animation.client;

import io.zershyan.sccore.animation.data.AnimationData;
import io.zershyan.sccore.animation.data.GenericAnimationData;
import io.zershyan.sccore.animation.utils.AnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow public abstract void setXRot(float pXRot);

    @Shadow private float xRot;

    @Shadow public abstract void setYRot(float pYRot);

    @Shadow private float yRot;

    @Shadow public abstract float getXRot();

    @Shadow public float xRotO;

    @Shadow public float yRotO;

    @Shadow public abstract float getYRot();

    @Inject(
            method = "turn",
            at = {@At(value = "HEAD")},
            cancellable = true
    )
    private void limitLyingType(double pYRot, double pXRot, CallbackInfo ci) {
        Entity self = Entity.class.cast(this);
        if(self instanceof AbstractClientPlayer player){
            AnimationData sideData = AnimationUtils.getLyingViewDefaultSide(player, null);
            if(sideData != null) {
                GenericAnimationData.LyingType lyingType = sideData.getLyingType();
                if(lyingType != null && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                    float f = (float)pXRot * 0.15F;
                    float f1 = (float)pYRot * 0.15F;
                    switch (lyingType) {
                        case LEFT -> {
                            this.setXRot(this.xRot + f1 * -1.0f);
                            this.setYRot(this.yRot + f);
                        }
                        case RIGHT -> {
                            this.setXRot(this.xRot + f1);
                            this.setYRot(this.yRot + f * -1.0f);
                        }
                    }
                    this.setXRot(Mth.clamp(this.getXRot(), 0.0f, 90.0f));
                    this.xRotO = this.xRot;
                    this.yRotO = this.yRot;
                    ci.cancel();
                }
            }
            AnimationData backData = AnimationUtils.getLyingViewDefaultSide(player, AnimationData.LyingType.BACK);
            if(backData != null) {
                AnimationData.LyingType lyingType = backData.getLyingType();
                if(lyingType != null && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                    float pitch = backData.getCamPitch();
                    float f = (float)pXRot * 0.15F;
                    float f1 = (float)pYRot * 0.15F;
                    this.setXRot(this.xRot + f);
                    this.setYRot(this.yRot + f1);
                    if(-90.0f <= pitch && pitch <= 0.0f) {
                        this.setXRot(Mth.clamp(this.getXRot(), pitch, 90.0f + pitch));
                    } else {
                        this.setXRot(Mth.clamp(this.getXRot(), -90.0f, 90.0f));
                    }
                    this.xRotO = this.xRot;
                    this.yRotO = this.yRot;
                    ci.cancel();
                }
            }
        }
    }
}
