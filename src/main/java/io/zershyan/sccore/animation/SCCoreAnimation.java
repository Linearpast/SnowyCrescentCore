package io.zershyan.sccore.animation;

import io.zershyan.sccore.animation.registry.AnimationAttachments;
import net.neoforged.bus.api.IEventBus;

public class SCCoreAnimation {
    public static void register(IEventBus modEventBus) {
        AnimationAttachments.register(modEventBus);
    }
}
