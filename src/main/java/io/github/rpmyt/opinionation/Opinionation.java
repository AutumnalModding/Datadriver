package io.github.rpmyt.opinionation;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Opinionation.MODID, version = Opinionation.VERSION)
public class Opinionation
{
    public static final String MODID = "opinionation";
    public static final String VERSION = "1.0";

    public static final Logger LOGGER = LogManager.getLogger("Opinionation");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
}
