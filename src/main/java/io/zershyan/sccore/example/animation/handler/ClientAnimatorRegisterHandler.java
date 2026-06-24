package io.zershyan.sccore.example.animation.handler;

import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.events.AnimationRegisterEvent;
import io.zershyan.sccore.animation.api.events.LayerRegisterEvent;
import io.zershyan.sccore.animation.data.ClientAnimation;
import net.neoforged.bus.api.SubscribeEvent;

public class ClientAnimatorRegisterHandler {
    @SubscribeEvent
    public static void registerLayerClient(LayerRegisterEvent.Client event) {
        event.registerLayer(SCCore.id("test_layer_client"), 45);
    }

    @SubscribeEvent
    public static void registerAnimationClient(AnimationRegisterEvent.Client event) {
        event.registerAnimation(SCCore.id("test_anim_client"), new ClientAnimation(
                SCCore.id("waltz_gentleman"), "华尔兹（男）", 0, null
        ));
    }
}
