package io.zershyan.sccore.patchouli.datagen.create.data.page;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LinkPage extends IPageType {
    @NotNull
    private final String url;
    @NotNull
    private final String linkText;

    public LinkPage(@NotNull String url, @NotNull String linkText) {
        super(new ResourceLocation("patchouli", "link"));
        this.url = url;
        this.linkText = linkText;
    }

    @Override
    public JsonObject toJson(JsonObject object) {
        object.addProperty("url", url);
        object.addProperty("link_text", linkText);
        return object;
    }
}
