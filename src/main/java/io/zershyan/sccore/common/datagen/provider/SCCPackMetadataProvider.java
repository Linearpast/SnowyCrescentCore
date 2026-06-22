package io.zershyan.sccore.common.datagen.provider;

import io.zershyan.sccore.common.datagen.init.SCCTranslatableLang;
import net.minecraft.DetectedVersion;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

public class SCCPackMetadataProvider extends PackMetadataGenerator {
    public SCCPackMetadataProvider(PackOutput pOutput) {
        super(pOutput);
        add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.translatable(SCCTranslatableLang.RESOURCES.getKey()),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA)
        ));
    }
}
