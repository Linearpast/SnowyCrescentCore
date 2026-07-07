package io.zershyan.sccore.mixin.playeranimator.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.zershyan.sccore.animation.core.ServerAnimationRegistry;
import io.zershyan.sccore.animation.data.Animation;
import io.zershyan.sccore.animation.handler.common.MovementAnimationTickHandler;
import io.zershyan.sccore.animation.network.data.MovementAnimationTickData;
import io.zershyan.sccore.api.SCCoreApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Mixin(Entity.class)
public class MixinEntity {
    @ModifyReturnValue(
            method = "getBoundingBox",
            at = @At(value = "RETURN")
    )
    private AABB redefinedBoundingBox(AABB original){
        Entity self = Entity.class.cast(this);
        if(!(self instanceof Player player)) return original;
        MovementAnimationTickData data = MovementAnimationTickHandler.getData(player.getUUID());
        if(data == null) return original;
        Optional<ResourceLocation> resourceLocation = data.animationId();
        if(resourceLocation.isEmpty()) return original;
        Animation animation = ServerAnimationRegistry.commonGetAnimation(resourceLocation.get());
        if(animation == null) return original;
        float partialTick = SCCoreApi.tryGetPartialTick(player.level());
        AABB aabb = sccore$getInterpolatedAABB(data.currentTick(), partialTick, animation.aabbMovement());
        if(aabb == null) return original;
        return original
                .setMinX(original.minX + aabb.minX)
                .setMinY(original.minY + aabb.minY)
                .setMinZ(original.minZ + aabb.minZ)
                .setMaxX(original.maxX + aabb.maxX)
                .setMaxY(original.maxY + aabb.maxY)
                .setMaxZ(original.maxZ + aabb.maxZ);
    }

    @Unique
    private static AABB sccore$getInterpolatedAABB(int tickCount, float partialTick, TreeMap<Integer, AABB> aabbDataTreeMap) {
        Map.Entry<Integer, AABB> exact = aabbDataTreeMap.floorEntry(tickCount);
        if (exact != null && exact.getKey() == tickCount) {
            return exact.getValue();
        }

        Map.Entry<Integer, AABB> lower = aabbDataTreeMap.floorEntry(tickCount);
        Map.Entry<Integer, AABB> higher = aabbDataTreeMap.ceilingEntry(tickCount);

        if (lower == null && higher == null) {
            return null;
        }
        if (lower == null) {
            return higher.getValue();
        }
        if (higher == null) {
            return lower.getValue();
        }

        int tickLow = lower.getKey();
        int tickHigh = higher.getKey();
        float t = ((tickCount - tickLow) + partialTick) / (tickHigh - tickLow);

        AABB aabbLow = lower.getValue();
        AABB aabbHigh = higher.getValue();

        return new AABB(
                Mth.lerp(t, (float) aabbLow.minX, (float) aabbHigh.minX),
                Mth.lerp(t, (float) aabbLow.minY, (float) aabbHigh.minY),
                Mth.lerp(t, (float) aabbLow.minZ, (float) aabbHigh.minZ),
                Mth.lerp(t, (float) aabbLow.maxX, (float) aabbHigh.maxX),
                Mth.lerp(t, (float) aabbLow.maxY, (float) aabbHigh.maxY),
                Mth.lerp(t, (float) aabbLow.maxZ, (float) aabbHigh.maxZ)
        );
    }
}