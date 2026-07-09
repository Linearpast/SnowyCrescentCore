package io.zershyan.sccore.example.animation.handler;

import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.SCCAnimationApi;
import io.zershyan.sccore.animation.api.events.AnimationRegisterEvent;
import io.zershyan.sccore.animation.api.events.LayerRegisterEvent;
import io.zershyan.sccore.animation.data.ClientAnimation;
import io.zershyan.sccore.animation.data.EulerAngle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.TreeMap;

public class ClientAnimatorRegisterHandler {
    private static final ResourceLocation testLayerClient = SCCore.id("test_layer_client");
    private static final ResourceLocation testAnimClient = SCCore.id("test_anim_client");
    @SubscribeEvent
    public static void registerLayerClient(LayerRegisterEvent.Client event) {
        event.registerLayer(testLayerClient, 45);
    }

    @SubscribeEvent
    public static void registerAnimationClient(AnimationRegisterEvent.Client event) {
        TreeMap<Integer, ClientAnimation.CameraData> movement = new TreeMap<>();
        movement.put(0, new ClientAnimation.CameraData(new Vec3(0,-1.5,0), new EulerAngle(0f, 90f,90f)));
        event.createAnimation(testAnimClient, SCCore.id("am_lying_to_right_lying"))
                .firstPersonCameraChange(new ClientAnimation.CameraChange(false, movement))
                .name("华尔兹（男）");
    }

    @SubscribeEvent
    public static void testPlayer(PlayerInteractEvent.LeftClickEmpty event) {
        if(event.getSide() == LogicalSide.CLIENT) {
            Player player = event.getEntity();
            SCCAnimationApi.animation(player).playAnimation(testLayerClient, testAnimClient);
        }
    }

    @SubscribeEvent
    public static void testPlayer1(PlayerInteractEvent.LeftClickBlock event) {
        if(event.getSide() == LogicalSide.CLIENT) {
            Player player = event.getEntity();
            SCCAnimationApi.animation(player).removeAnimation(testLayerClient);
        }
    }
}
