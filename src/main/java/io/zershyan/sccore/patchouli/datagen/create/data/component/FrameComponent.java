package io.zershyan.sccore.patchouli.datagen.create.data.component;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class FrameComponent extends ITemplateComponent {
    public FrameComponent() {
        super(ResourceLocation.fromNamespaceAndPath("patchouli", "frame"));
    }

    @Override
    public JsonObject toJson(JsonObject object) {
        return object;
    }
}
