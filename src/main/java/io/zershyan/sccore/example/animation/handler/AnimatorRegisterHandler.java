package io.zershyan.sccore.example.animation.handler;

import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.api.events.AnimationRegisterEvent;
import io.zershyan.sccore.animation.api.events.LayerRegisterEvent;
import io.zershyan.sccore.animation.data.RideData;
import io.zershyan.sccore.animation.data.ServerAnimation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.util.List;
import java.util.Optional;

public class AnimatorRegisterHandler {
    private static final ResourceLocation testLayer = SCCore.id("test_layer");
    private static final ResourceLocation testAnim = SCCore.id("test_anim");

    @SubscribeEvent
    public static void registerLayer(LayerRegisterEvent.Server event) {
        event.registerLayer(testLayer, 44);
    }

    @SubscribeEvent
    public static void registerAnimation(AnimationRegisterEvent.Server event) {

        event.registerAnimation(testAnim, new ServerAnimation(
                SCCore.id("waltz_lady"), Optional.of("华尔兹（女）"), 0,
                Optional.of(new RideData(List.of(), Vec3.ZERO, 50, 0, 0)), true
        ));
    }

    @SubscribeEvent
    public static void testPlay(AttackEntityEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            if(event.getTarget() instanceof Sheep) {
                SCCAnimationApi.ridePlayer(player).startRide(testLayer, testAnim);
            }
        }
    }
}
