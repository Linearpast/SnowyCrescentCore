package io.zershyan.sccore.example.patchouli;

import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.example.patchouli.datagen.SCCPatchouliDataGen;
import net.neoforged.bus.api.IEventBus;

public class ExamplePatchouli {
    /**
     * @see SCCore.Common#Common
     * @param forgeBus
     * @param modBus
     */
    public static void register(IEventBus forgeBus, IEventBus modBus) {
        modBus.addListener(SCCPatchouliDataGen::gatherData);
    }
}
