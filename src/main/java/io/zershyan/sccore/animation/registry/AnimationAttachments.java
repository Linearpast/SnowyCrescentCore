package io.zershyan.sccore.animation.registry;

import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.registry.attachment.AnimationData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AnimationAttachments {
    private static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SCCore.MODID);

    public static final Supplier<AttachmentType<AnimationData>> ANIMATION_DATA = REGISTRY.register(
            "animation_data", () -> AttachmentType.builder(AnimationData::new)
                    .serialize(AnimationData.CODEC)
                    .sync(AnimationData.STREAM_CODEC)
                    .copyOnDeath()
                    .build());

    public static void register(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }

}
