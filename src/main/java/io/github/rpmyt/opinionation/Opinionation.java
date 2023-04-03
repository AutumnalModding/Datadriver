package io.github.rpmyt.opinionation;

import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = Opinionation.MODID, version = Opinionation.VERSION)
public class Opinionation
{
    public static final String MODID = "opinionation";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        System.out.println("Opinionating your game...");
    }
}
