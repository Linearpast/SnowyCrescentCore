package io.zershyan.sccore.patchouli.datagen.create.data;

import com.google.gson.JsonObject;
import io.zershyan.sccore.patchouli.datagen.create.data.format.ConfigFlags;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface IPatchouliCategoryData {
    @NotNull ResourceLocation getId();

    IPatchouliCategoryData parent(IPatchouliCategoryData parent);

    IPatchouliCategoryData parent(ResourceLocation parent);

    IPatchouliCategoryData flag(ConfigFlags flag);

    IPatchouliCategoryData sortNum(Integer sortNum);

    IPatchouliCategoryData secret(Boolean secret);

    JsonObject serialize();
}
