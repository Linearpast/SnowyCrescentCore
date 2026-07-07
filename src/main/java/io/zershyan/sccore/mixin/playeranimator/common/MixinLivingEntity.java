package io.zershyan.sccore.mixin.playeranimator.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.core.ServerAnimationRegistry;
import io.zershyan.sccore.animation.data.Animation;
import io.zershyan.sccore.animation.data.ServerAnimation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Shadow
    protected abstract float getJumpPower(float multiplier);

    @WrapOperation(
            method = "jumpFromGround",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getJumpPower()F")
    )
    public float modifyJumpPower(LivingEntity instance, Operation<Float> original) {
        modifyJumpPower: {
            if(!(instance instanceof Player player)) break modifyJumpPower;
            Optional<Map.Entry<ResourceLocation, ResourceLocation>> max = SCCAnimationApi.animation(player).getHighestPriorityAnimation();
            if(max.isEmpty()) break modifyJumpPower;
            ResourceLocation value = max.get().getValue();
            Animation animation = ServerAnimationRegistry.commonGetAnimation(value);
            if(!(animation instanceof ServerAnimation serverAnimation)) break modifyJumpPower;
            return getJumpPower(serverAnimation.jumpModifier());
        }
        return original.call(instance);
    }
}
