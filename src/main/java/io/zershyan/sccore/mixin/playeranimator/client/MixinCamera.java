package io.zershyan.sccore.mixin.playeranimator.client;

import io.zershyan.sccore.animation.data.EulerAngle;
import io.zershyan.sccore.animation.handler.client.CameraTransformStateHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Shadow
    protected abstract void setRotation(float yaw, float pitch, float roll);

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    public abstract Vector3f getLookVector();

    @Shadow
    public abstract Vector3f getUpVector();

    @Shadow
    public abstract Vector3f getLeftVector();

    @Inject(
            method = "setup",
            at = @At("TAIL")
    )
    public void afterSetup(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        Minecraft instance = Minecraft.getInstance();
        LocalPlayer player = instance.player;
        if (player == null) return;

        boolean firstPerson = instance.options.getCameraType().isFirstPerson();
        CameraTransformStateHandler.Snapshot cur = CameraTransformStateHandler.get(player.getUUID(), firstPerson);
        if(cur == CameraTransformStateHandler.Snapshot.ZERO) return;

        EulerAngle euler = CameraTransformStateHandler.lerpEuler(cur.old, cur, partialTick);
        Vec3 offset = CameraTransformStateHandler.lerpOffset(cur.old, cur, partialTick);

        float yaw = player.getViewYRot(partialTick) + euler.yaw();
        float pitch = player.getViewXRot(partialTick) + euler.pitch();
        float roll = euler.roll();
        setRotation(yaw, pitch, roll);

        if (cur.relative) {
            Vector3f look = getLookVector();
            Vector3f up = getUpVector();
            Vector3f right = getLeftVector();
            Vec3 forward = new Vec3(look.x(), look.y(), look.z());
            Vec3 upVec = new Vec3(up.x(), up.y(), up.z());
            Vec3 rightVec = new Vec3(-right.x(), -right.y(), -right.z());
            Vec3 worldOffset = forward.scale(offset.y)
                    .add(upVec.scale(offset.z))
                    .add(rightVec.scale(offset.x));
            Vec3 pos = player.getEyePosition(partialTick).add(worldOffset);
            setPosition(pos.x, pos.y, pos.z);
        } else {
            Vec3 pos = player.getEyePosition(partialTick).add(offset);
            setPosition(pos.x, pos.y, pos.z);
        }
    }
}
