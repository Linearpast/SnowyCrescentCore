package io.zershyan.sccore.patchouli.datagen.create.data.page;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CraftingRecipePage extends RecipePage{
    public CraftingRecipePage(@NotNull ResourceLocation recipe) {
        super(new ResourceLocation("patchouli", "crafting"), recipe);
    }
}
