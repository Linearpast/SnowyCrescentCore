package io.zershyan.sccore.mixin;

import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SCCoreMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    private static final String[] mixinMods = {
            "playeranimator"
    };

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        List<ModInfo> modInfos = LoadingModList.get().getMods();
        List<String> modList = modInfos.stream().map(ModInfo::getModId).toList();
        for (String modid : mixinMods) {
            if (mixinClassName.startsWith(this.getClass().getPackageName() + "." + modid + ".")) {
                return modList.contains(modid);
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
