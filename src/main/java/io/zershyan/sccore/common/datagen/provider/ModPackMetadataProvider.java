package io.zershyan.sccore.common.datagen.provider;

import io.zershyan.sccore.common.datagen.ModLang;
import net.minecraft.DetectedVersion;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModPackMetadataProvider extends PackMetadataGenerator {
    public ModPackMetadataProvider(PackOutput pOutput) {
        super(pOutput);
        add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.translatable(ModLang.TranslatableMessage.RESOURCES.getKey()),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
                Arrays.stream(PackType.values()).collect(
                        Collectors.toMap(Function.identity(), DetectedVersion.BUILT_IN::getPackVersion)
                )
        ));
    }
}
