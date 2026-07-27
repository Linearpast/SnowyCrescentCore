package io.zershyan.sccore.animation.handler.client;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.api.data.AnimationHelper;
import io.zershyan.sccore.animation.core.ClientAnimationRegistry;
import io.zershyan.sccore.animation.data.ClientAnimation;
import io.zershyan.sccore.animation.data.camera.CameraChange;
import io.zershyan.sccore.animation.data.camera.CameraData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber(modid = SCCore.MODID, value = Dist.CLIENT)
public final class CameraTransformStateHandler {
    private static final Map<UUID, SnapShot> SnapShots = new HashMap<>();
    public static class SnapShot {
        @Nullable ClientAnimation curAnimation;
        @Nullable KeyframeAnimationPlayer curKeyframeAnimation;
        @Nullable CameraData cacheFirst;
        @Nullable CameraData cacheThird;

        SnapShot() {
            this.curAnimation = null;
            this.curKeyframeAnimation = null;
            this.cacheFirst = null;
            this.cacheThird = null;
        }

        public @Nullable ClientAnimation getCurAnimation() {
            return curAnimation;
        }

        public void setCurAnimation(@Nullable ClientAnimation curAnimation) {
            this.curAnimation = curAnimation;
        }

        public @Nullable KeyframeAnimationPlayer getCurKeyframeAnimation() {
            return curKeyframeAnimation;
        }

        public void setCurKeyframeAnimation(@Nullable KeyframeAnimationPlayer curKeyframeAnimation) {
            this.curKeyframeAnimation = curKeyframeAnimation;
        }

        public void clearAnimation() {
            this.curAnimation = null;
            this.curKeyframeAnimation = null;
        }

        public @Nullable CameraData getCache(boolean firstPerson) {
            return firstPerson ? cacheFirst : cacheThird;
        }

        public @Nullable CameraData get(float partialTick, boolean firstPerson) {
            CameraData cache = firstPerson ? cacheFirst : cacheThird;
            if(curAnimation == null) curKeyframeAnimation = null;
            if(curKeyframeAnimation == null && CameraData.isOrNearEmpty(cache)) {
                curAnimation = null;
                return firstPerson ? (cacheFirst = null) : (cacheThird = null);
            }
            CameraData absData = calAbsolutionCurrentData(partialTick, firstPerson);
            if (absData == null) absData = CameraData.ZERO;
            if (cache == null) cache = absData;

            float deltaTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
            float delta = deltaTicks / 5.0F;
            if (delta == 0.0F) delta = 0.0022857143F;
            CameraData sample = cache.sample(delta, absData);
            return firstPerson ? (cacheFirst = sample) : (cacheThird = sample);
        }

        public @Nullable CameraData calAbsolutionCurrentData(float partialTick, boolean firstPerson) {
            if(curKeyframeAnimation == null) return null;
            CameraChange cameraChange = getCameraChange(firstPerson);
            if(cameraChange == null) return null;
            return cameraChange.sample(curKeyframeAnimation.getCurrentTick() + partialTick);
        }

        public boolean relativeEuler(boolean firstPerson) {
            if(curKeyframeAnimation == null) return true;
            CameraChange cameraChange = getCameraChange(firstPerson);
            if(cameraChange == null) return true;
            return cameraChange.relativeEuler();
        }

        private @Nullable CameraChange getCameraChange(boolean firstPerson) {
            if(curAnimation == null) return null;
            if(curKeyframeAnimation == null) return null;
            if(!curAnimation.hasCameraChange()) return null;
            return firstPerson ? curAnimation.firstPersonCameraChange() : curAnimation.cameraChange();
        }
    }

    /**
     * 每客户端 tick 仅锁定当前激活的相机变换动画与 tick，供渲染端采样 cur1。
     * 不再在此采样 cur——cur 由渲染端用 tick + partialTick 实时插值得到，
     * 相机位置由显示缓冲区 {@code display} 按帧时间 delta 逐帧朝 cur1 追赶，
     * 这样动画切换时 cur1 即刻跳到新动画，display 仅以 delta 比例平滑追赶，避免瞬间重置。
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft instance = Minecraft.getInstance();
        if (instance.level == null) return;
        for (AbstractClientPlayer player : instance.level.players()) {
            samplePlayer(player);
        }
    }

    private static void samplePlayer(AbstractClientPlayer player) {
        AnimationHelper helper = SCCAnimationApi.animation(player);
        Optional<Map.Entry<ResourceLocation, ResourceLocation>> max = helper.getHighestPriorityAnimation(animation -> {
            if (animation instanceof ClientAnimation clientAnimation) {
                return clientAnimation.hasCameraChange();
            } else return false;
        });

        SnapShot snapShot = SnapShots.computeIfAbsent(player.getUUID(), uuid -> new SnapShot());
        testValid: {
            if (max.isEmpty()) break testValid;
            Map.Entry<ResourceLocation, ResourceLocation> entry = max.get();
            ClientAnimation animation = ClientAnimationRegistry.getAnimation(entry.getValue());
            if (animation == null) break testValid;
            KeyframeAnimationPlayer currentAnimation = SCCAnimationApi.animPlayer(player).getKeyframeAnimationPlayer(entry.getKey());
            if (currentAnimation == null) break testValid;

            snapShot.setCurAnimation(animation);
            snapShot.setCurKeyframeAnimation(currentAnimation);
            return;
        }
        snapShot.clearAnimation();
    }

    public static boolean relativeEuler(Player player, boolean firstPerson) {
        SnapShot snapShot = SnapShots.get(player.getUUID());
        if (snapShot == null) return true;
        return snapShot.relativeEuler(firstPerson);
    }

    public static @Nullable CameraData getCache(Player player, boolean firstPerson) {
        SnapShot snapShot = SnapShots.get(player.getUUID());
        if (snapShot == null) return null;
        return snapShot.getCache(firstPerson);
    }

    public static @Nullable CameraData getAndStep(Player player, float partialTick, boolean firstPerson) {
        SnapShot snapShot = SnapShots.get(player.getUUID());
        if (snapShot == null) return null;
        return snapShot.get(partialTick, firstPerson);
    }
}
