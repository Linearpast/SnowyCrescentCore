package com.linearpast.snowy_crescent_core;


import com.linearpast.snowy_crescent_core.capability.PlayerCapabilityUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;


@Mod(SnowyCrescentCore.MODID)
public class SnowyCrescentCore {

    public static final String MODID = "snowy_crescent_core";

    public SnowyCrescentCore() {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        PlayerCapabilityUtils.registerHandler(forgeBus);
    }
}
