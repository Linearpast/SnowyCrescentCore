package io.zershyan.sccore.example.patchouli;

import io.zershyan.sccore.SnowyCrescentCore;
import io.zershyan.sccore.example.patchouli.datagen.PatchouliDataGen;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModPatchouli {
    /**
     * @see SnowyCrescentCore#SnowyCrescentCore()
     * @param forgeBus
     * @param modBus
     */
    public static void register(IEventBus forgeBus, IEventBus modBus) {
        modBus.addListener(PatchouliDataGen::gatherData);
    }
}
