package io.zershyan.sccore.example.animation.handler;

import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.events.AnimationRegisterEvent;
import io.zershyan.sccore.animation.api.events.LayerRegisterEvent;
import io.zershyan.sccore.animation.data.ServerAnimation;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Optional;

public class AnimatorRegisterHandler {
    @SubscribeEvent
    public static void registerLayer(LayerRegisterEvent.Server event) {
        event.registerLayer(SCCore.id("test_layer"), 44);
    }

    @SubscribeEvent
    public static void registerAnimation(AnimationRegisterEvent.Server event) {
        event.registerAnimation(SCCore.id("test_anim"), new ServerAnimation(
                SCCore.id("waltz_lady"), Optional.of("华尔兹（女）"), 0, Optional.empty()
        ));
    }
}
