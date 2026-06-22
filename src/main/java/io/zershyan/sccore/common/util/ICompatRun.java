package io.zershyan.sccore.common.util;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public abstract class ICompatRun implements ILazyRun {
    private final String modId;
    public ICompatRun(String modId) {
        this.modId = modId;
    }

    @Override
    public boolean testCondition() {
        return ModList.get().isLoaded(modId);
    }

    public void addCommonListener(IEventBus forgeBus, IEventBus modBus){}
    public void addClientListener(IEventBus forgeBus, IEventBus modBus){}
    public void testLoadedAndAddListener(IEventBus forgeBus, IEventBus modBus) {
        if(testCondition()){
            addCommonListener(forgeBus, modBus);
            if(FMLLoader.getDist() == Dist.CLIENT){
                addClientListener(forgeBus, modBus);
            }
        }
    }
}
