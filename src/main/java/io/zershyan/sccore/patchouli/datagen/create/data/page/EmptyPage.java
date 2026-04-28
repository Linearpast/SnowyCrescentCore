package io.zershyan.sccore.patchouli.datagen.create.data.page;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class EmptyPage extends IPageType {
    private Boolean drawFiller;

    public EmptyPage() {
        super(new ResourceLocation("patchouli", "empty"));
    }

    public EmptyPage drawFiller(Boolean drawFiller) {
        this.drawFiller = drawFiller;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject object) {
        if (drawFiller != null) {
            object.addProperty("drawFiller", drawFiller);
        }
        return object;
    }
}
