package io.zershyan.sccore.patchouli.datagen.create.data.format;

import net.minecraft.resources.ResourceLocation;

public class RLFormat extends StringFormat {
    public RLFormat(ResourceLocation value) {
        super(value.toString());
    }
}
