package io.zershyan.sccore.example.patchouli.datagen;

import io.zershyan.sccore.example.patchouli.datagen.provider.ModPatchouliBookProvider;
import io.zershyan.sccore.example.patchouli.datagen.provider.ModPatchouliContentProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Gather data event
 */
public class PatchouliDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        //In 1.20.1, content will generate in directory "data". So use event.includeServer().
        generator.addProvider(event.includeServer(), new ModPatchouliBookProvider(packOutput, helper, lookupProvider));
        //In 1.20.1, content will generate in directory "assets". So use event.includeClient().
        generator.addProvider(event.includeClient(), new ModPatchouliContentProvider(packOutput, helper, lookupProvider));
    }
}
