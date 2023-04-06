package io.github.rpmyt.finetune;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = FinetuneInit.MODID, version = FinetuneInit.VERSION)
public class FinetuneInit
{
    public static final String MODID = "finetune";
    public static final String VERSION = "1.0";

    public static final Logger LOGGER = LogManager.getLogger("finetune");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FinetuneConfig.synchronizeConfiguration(event.getSuggestedConfigurationFile());
    }
}
