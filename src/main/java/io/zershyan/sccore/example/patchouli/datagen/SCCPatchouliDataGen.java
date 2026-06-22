package io.zershyan.sccore.example.patchouli.datagen;

import io.zershyan.sccore.example.patchouli.datagen.provider.SCCPatchouliBookProvider;
import io.zershyan.sccore.example.patchouli.datagen.provider.SCCPatchouliContentProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Gather data event
 */
public class SCCPatchouliDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        //In 1.21.1, book will generate in directory "assets".
        event.createProvider(SCCPatchouliBookProvider::new);
        //In 1.21.1, content will generate in directory "data".
        event.createProvider(SCCPatchouliContentProvider::new);
    }
}
