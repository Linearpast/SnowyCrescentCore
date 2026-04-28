package io.zershyan.sccore.patchouli.datagen.create.data;

import com.google.gson.JsonObject;
import io.zershyan.sccore.patchouli.datagen.create.data.component.ITemplateComponent;
import io.zershyan.sccore.patchouli.datagen.create.data.format.TemplateInclude;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface IPatchouliTemplateData {
    @NotNull ResourceLocation getId();

    IPatchouliTemplateData addComponents(ITemplateComponent... components);

    IPatchouliTemplateData components(ITemplateComponent... components);

    IPatchouliTemplateData include(TemplateInclude include);

    IPatchouliTemplateData processor(Class<?> processor);

    JsonObject serialize();
}
