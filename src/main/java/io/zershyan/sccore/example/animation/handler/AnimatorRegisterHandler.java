package io.zershyan.sccore.example.animation.handler;

import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.api.events.AnimationRegisterEvent;
import io.zershyan.sccore.animation.api.events.LayerRegisterEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.util.TreeMap;

public class AnimatorRegisterHandler {
    private static final ResourceLocation testLayer = SCCore.id("test_layer");
    private static final ResourceLocation testAnim = SCCore.id("test_anim");

    @SubscribeEvent
    public static void registerLayer(LayerRegisterEvent.Server event) {
        event.registerLayer(testLayer, 44);
    }

    @SubscribeEvent
    public static void registerAnimation(AnimationRegisterEvent.Server event) {

        TreeMap<Integer, AABB> aabbMovement = new TreeMap<>();
        aabbMovement.put(0, new AABB(Vec3.ZERO, Vec3.ZERO));
        aabbMovement.put(49, new AABB(Vec3.ZERO.add(5,5,5), Vec3.ZERO.add(5,5,5)));
        event.createAnimation(testAnim, SCCore.id("waltz_lady"))
                .rideData(builder -> builder.existTick(50))
                .aabbMovement(aabbMovement)
                .defaultThirdPerson(true)
                .name("华尔兹（女）");
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
