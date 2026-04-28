package io.zershyan.sccore.patchouli.datagen.create;

import io.zershyan.sccore.patchouli.datagen.create.data.IPatchouliBookData;
import io.zershyan.sccore.patchouli.datagen.create.data.PatchouliBookData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class PatchouliBookProvider implements DataProvider {

    private final String modId;
    private final ExistingFileHelper fileHelper;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final PackOutput packOutput;
    private final Map<String, IPatchouliBookData> bookBuilders = new HashMap<>();

    private static final String patchouliDirectory = "patchouli_books";

    public PatchouliBookProvider(String modId, PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
        this.modId = modId;
        this.fileHelper = fileHelper;
        this.packOutput = output;
        this.registries = registries;
    }

    protected abstract void addBooks(HolderLookup.Provider provider, ExistingFileHelper fileHelper);

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        return this.registries.thenCompose(provider -> {
            List<CompletableFuture<?>> list = new ArrayList<>();
            this.addBooks(provider, this.fileHelper);
            this.bookBuilders.forEach((key, value) -> {
                Path path = this.packOutput.createPathProvider(PackOutput.Target.DATA_PACK, patchouliDirectory)
                        .json(new ResourceLocation(this.modId, key + "/book"));
                list.add(DataProvider.saveStable(pOutput, value.serialize(), path));
            });
            return CompletableFuture.allOf(list.toArray(new CompletableFuture<?>[0]));
        });
    }

    public final IPatchouliBookData createBook(String directory, String name, String landingText) {
        return this.bookBuilders.computeIfAbsent(directory, s -> new PatchouliBookData(name, landingText));
    }

    public final IPatchouliBookData createBook(Item item, String landingText) {
        return createBook(item.toString(), item.getDescriptionId(), landingText);
    }

    @Override
    public @NotNull String getName() {
        return "Patchouli book for " + this.modId;
    }
}
