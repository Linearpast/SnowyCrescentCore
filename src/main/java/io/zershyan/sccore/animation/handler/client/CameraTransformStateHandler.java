package io.zershyan.sccore.animation.handler.client;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.api.data.AnimationHelper;
import io.zershyan.sccore.animation.core.ClientAnimationRegistry;
import io.zershyan.sccore.animation.data.ClientAnimation;
import io.zershyan.sccore.animation.data.EulerAngle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber(modid = SCCore.MODID, value = Dist.CLIENT)
public final class CameraTransformStateHandler {
    private static final Map<UUID, PlayerEntry> SNAPSHOTS = new HashMap<>();

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
        Optional<Map.Entry<ResourceLocation, ResourceLocation>> max = helper.getHighestPriorityAnimation();
        if (max.isEmpty()) {
            clear(player.getUUID());
            return;
        }
        Map.Entry<ResourceLocation, ResourceLocation> entry = max.get();
        ClientAnimation animation = ClientAnimationRegistry.getAnimation(entry.getValue());
        if (animation == null) {
            clear(player.getUUID());
            return;
        }
        KeyframeAnimationPlayer currentAnimation = SCCAnimationApi.animPlayer(player).getKeyframeAnimationPlayer(entry.getKey());
        if (currentAnimation == null) {
            clear(player.getUUID());
            return;
        }
        int tick = currentAnimation.getCurrentTick();

        PlayerEntry e = SNAPSHOTS.computeIfAbsent(player.getUUID(), k -> new PlayerEntry());
        e.first = rollSnapshot(e.first, animation.firstPersonCameraChange(), tick);
        e.third = rollSnapshot(e.third, animation.cameraChange(), tick);
    }

    private static Snapshot rollSnapshot(Snapshot previous, ClientAnimation.CameraChange change, int tick) {
        Snapshot snap = sample(change, tick);
        if (snap != null) {
            snap.old = previous;
            return snap;
        }
        Snapshot inactive = new Snapshot(change.relative(), Vec3.ZERO, EulerAngle.ZERO);
        inactive.old = previous;
        return inactive;
    }

    private static Snapshot sample(ClientAnimation.CameraChange change, int tick) {
        if (change == null || change.movement().isEmpty()) return null;
        ClientAnimation.CameraData data = change.sample((float) tick);
        if (data == null) return null;
        return new Snapshot(change.relative(), data.offset(), data.camEulerAngles());
    }

    private static void clear(UUID uuid) {
        SNAPSHOTS.remove(uuid);
    }

    public static Snapshot get(UUID uuid, boolean firstPerson) {
        PlayerEntry e = SNAPSHOTS.get(uuid);
        if (e == null) return Snapshot.ZERO;
        return firstPerson ? (e.first != null ? e.first : Snapshot.ZERO) : (e.third != null ? e.third : Snapshot.ZERO);
    }

    public static class PlayerEntry {
        Snapshot first;
        Snapshot third;
    }

    public static class Snapshot {
        public static final Snapshot ZERO = new Snapshot(false, Vec3.ZERO, EulerAngle.ZERO);

        public final boolean relative;
        public final Vec3 offset;
        public final EulerAngle euler;
        public Snapshot old;

        Snapshot(boolean relative, Vec3 offset, EulerAngle euler) {
            this.relative = relative;
            this.offset = offset;
            this.euler = euler;
        }
    }

    public static EulerAngle lerpEuler(CameraTransformStateHandler.Snapshot old, CameraTransformStateHandler.Snapshot cur, float p) {
        if (old == null) return cur.euler;
        return new EulerAngle(
                Mth.lerp(p, old.euler.pitch(), cur.euler.pitch()),
                Mth.lerp(p, old.euler.yaw(), cur.euler.yaw()),
                Mth.lerp(p, old.euler.roll(), cur.euler.roll())
        );
    }

    public static Vec3 lerpOffset(CameraTransformStateHandler.Snapshot old, CameraTransformStateHandler.Snapshot cur, float p) {
        if (old == null) return cur.offset;
        return new Vec3(
                Mth.lerp(p, old.offset.x, cur.offset.x),
                Mth.lerp(p, old.offset.y, cur.offset.y),
                Mth.lerp(p, old.offset.z, cur.offset.z)
        );
    }
}
