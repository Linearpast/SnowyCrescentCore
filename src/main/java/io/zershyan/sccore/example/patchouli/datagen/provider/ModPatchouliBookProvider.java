package io.zershyan.sccore.example.patchouli.datagen.provider;

import io.zershyan.sccore.SnowyCrescentCore;
import io.zershyan.sccore.patchouli.datagen.create.PatchouliBookProvider;
import io.zershyan.sccore.patchouli.datagen.create.data.IPatchouliBookData;
import io.zershyan.sccore.patchouli.datagen.create.data.format.ItemFormat;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModPatchouliBookProvider extends PatchouliBookProvider {
    public ModPatchouliBookProvider(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
        super(SnowyCrescentCore.MODID, output, fileHelper, registries);
    }

    /**
     * You can see a link: <a href="https://vazkiimods.github.io/Patchouli/">Patchouli WIKI</a>
     * @param provider provider
     * @param fileHelper fileHelper
     * @see IPatchouliBookData
     */
    @Override
    protected void addBooks(HolderLookup.Provider provider, ExistingFileHelper fileHelper) {
        IPatchouliBookData bookData = createBook("lexicon",  "This is book name.", "This is landing text.")
                .i18n(true).indexIcon(ItemFormat.of(Items.ENCHANTED_BOOK)).allowExtensions(true).showProgress(false)
                .creativeTab(new ResourceLocation("food_and_drinks"))
                .bookTexture(new ResourceLocation("patchouli", "textures/gui/book_purple.png"))
                .dontGenerateBook(false);
    }
}
