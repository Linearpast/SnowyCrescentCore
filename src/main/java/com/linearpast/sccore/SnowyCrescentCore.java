package com.linearpast.sccore;


import com.linearpast.sccore.capability.CapabilityUtils;
import com.linearpast.sccore.example.ModCaps;
import com.linearpast.sccore.network.Channel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;


@Mod(SnowyCrescentCore.MODID)
public class SnowyCrescentCore {

    public static final String MODID = "sccore";

    public SnowyCrescentCore() {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        CapabilityUtils.registerHandler(forgeBus);
        Channel.register();

        if(!FMLEnvironment.production){
            ModCaps.register();
            ModCaps.addListenerToEvent(forgeBus);
        }
    }
}
