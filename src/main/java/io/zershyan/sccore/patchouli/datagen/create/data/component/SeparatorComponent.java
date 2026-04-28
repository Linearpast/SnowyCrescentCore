package io.zershyan.sccore.patchouli.datagen.create.data.component;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class SeparatorComponent extends ITemplateComponent {
    public SeparatorComponent() {
        super(new ResourceLocation("patchouli", "separator"));
    }

    @Override
    public JsonObject toJson(JsonObject object) {
        return object;
    }
}
