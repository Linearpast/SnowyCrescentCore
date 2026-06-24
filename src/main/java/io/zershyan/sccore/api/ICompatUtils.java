package io.zershyan.sccore.api;

import io.zershyan.sccore.SCCore;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.concurrent.Callable;

public abstract class ICompatUtils {
    private final String modid;
    protected ICompatUtils(String modid) {
        this.modid = modid;
    }

    public final boolean testLoadedAndRun(Runnable runnable){
        if(isModLoaded()) runnable.run();
        else return false;
        return true;
    }

    public final <T> T testLoadedAndCall(Callable<T> callable, T errorResult) {
        try {
            if(isModLoaded()) return callable.call();
        } catch (Exception ignored) {}
        return errorResult;
    }

    public final <T> T testLoadedAndCall(Callable<T> callable, Callable<T> elseCall, T errorResult) {
        try {
            if(isModLoaded()) return callable.call();
            else return elseCall.call();
        }catch(Exception e) {
            return errorResult;
        }
    }

    protected void addCommonListener(IEventBus forgeBus, IEventBus modBus){}
    protected void addClientListener(IEventBus forgeBus, IEventBus modBus){}
    private void addListener(IEventBus forgeBus, IEventBus modBus) {
        addCommonListener(forgeBus, modBus);
        if(FMLLoader.getDist() == Dist.CLIENT){
            addClientListener(forgeBus, modBus);
        }
    }

    protected void init(IEventBus forgeBus, IEventBus modBus){
        addListener(forgeBus, modBus);
    }

    protected void registerNetwork(PayloadRegistrar registrar) { }

    public final void initNetwork(PayloadRegistrar registrar) {
        try { testLoadedAndRun(() -> registerNetwork(registrar)); }
        catch (Exception e) { SCCore.log.error(e.getMessage()); }
    }

    public final void initial(IEventBus forgeBus, IEventBus modBus) {
        try { testLoadedAndRun(() -> init(forgeBus, modBus)); }
        catch (Exception e) { SCCore.log.error(e.getMessage()); }
    }

    public boolean isModLoaded() {
        return ModList.get().isLoaded(modid);
    }
}
