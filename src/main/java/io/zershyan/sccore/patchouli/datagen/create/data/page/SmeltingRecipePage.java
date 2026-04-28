package io.zershyan.sccore.patchouli.datagen.create.data.page;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SmeltingRecipePage extends RecipePage{
    public SmeltingRecipePage(@NotNull ResourceLocation recipe) {
        super(new ResourceLocation("patchouli", "smelting"), recipe);
    }
}
