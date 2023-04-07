package io.github.rpmyt.finetune;

import java.io.File;
import java.util.HashMap;

import cpw.mods.fml.common.registry.GameRegistry;
import io.github.rpmyt.finetune.mixin.plugin.TargetedMod;
import io.github.rpmyt.finetune.util.RequiresMod;
import lotr.common.item.LOTRWeaponStats;
import net.minecraft.item.Item;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

public class FinetuneConfig {
    public static class LOTR {
        public static boolean UNLOCK_COSMETICS = false;
        public static boolean MAKE_NPCS_NEUTRAL = false;
        public static boolean REMOVE_COMBAT = false;
        public static boolean UNLOCK_WAYPOINTS = false;
        public static boolean NO_WAYPOINT_LOCKING = false;
    }

    public static class Botania {
        public static int GAIA_DAMAGE_CAP = 40;
        public static int GAIA_DAMAGE_CAP_CRIT = 60;

        public static class GenerationAmounts {
            public static int ARCANE_ROSE = 50;
            public static int SPECTROLUS = 300;
            public static int NARSLIMMUS = 820;
        }

        public static class ManaCapacities {
            public static int ARCANE_ROSE = 6000;
            public static int SPECTROLUS = 8000;
            public static int NARSLIMMUS = 8000;
        }
    }

    public static class Thaumcraft {
        public static int ELDRITCH_BOSS_DAMAGE_CAP = 35;
    }

    @RequiresMod(TargetedMod.LOTR)
    private static void addCombatItems(String[] items) {
        for (String entry : items) {
            float speed = Float.parseFloat(entry.replaceAll(".*@", "").replaceAll("_.*", ""));
            float reach = Float.parseFloat(entry.replaceAll(".*_", ""));

            String modID = entry.replaceAll(":.*", "");
            String itemID = entry.replaceAll(".*:", "").replaceAll("@.*", "");

            Item item = GameRegistry.findItem(modID, itemID);

            if (item != null) {
                FinetuneInit.LOGGER.info("Registering item '" + modID + ":" + itemID + "' to the LOTR combat system (speed " + speed + "x, reach " + reach + "x)...");
                LOTRWeaponStats.registerMeleeSpeed(item, speed);
                LOTRWeaponStats.registerMeleeReach(item, reach);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        {
            LOTR.UNLOCK_COSMETICS = configuration.getBoolean("unlockCosmetics", "lotr", LOTR.UNLOCK_COSMETICS, "Unlock all player-specific LOTR cosmetics?");
            LOTR.MAKE_NPCS_NEUTRAL = configuration.getBoolean("makeNpcsNeutral", "lotr", LOTR.MAKE_NPCS_NEUTRAL, "Make all LOTR NPCs neutral?");
            LOTR.REMOVE_COMBAT = configuration.getBoolean("removeShittyCombat", "lotr", LOTR.REMOVE_COMBAT, "Remove the 1.9-style combat changes?");
            LOTR.UNLOCK_WAYPOINTS = configuration.getBoolean("unlockAllWaypoints", "lotr", LOTR.UNLOCK_WAYPOINTS, "Unlock all fast travel waypoints?");
            LOTR.NO_WAYPOINT_LOCKING = configuration.getBoolean("disableWaypointLocking", "lotr", LOTR.NO_WAYPOINT_LOCKING, "Disable alignment-based waypoint locking?");

            String[] items = configuration.getStringList("additionalCombatItems", "lotr", new String[]{"minecraft:golden_axe@1.5_1.0"},
                    "List of items to add to the custom LOTR combat system.\n" +
                            "Format: 'modid:item_name@speedMultiplier_reachMultiplier'\n" +
                            "Example: 'minecraft:golden_axe@1.5_1.0"
            );

            addCombatItems(items);
        } // LOTR stuff
        {
            Botania.GAIA_DAMAGE_CAP = configuration.getInt("gaiaDamageCap", "botania", Botania.GAIA_DAMAGE_CAP, 1, Integer.MAX_VALUE, "Gaia Guardian damage cap");
            Botania.GAIA_DAMAGE_CAP_CRIT = configuration.getInt("gaiaDamageCapCrit", "botania", Botania.GAIA_DAMAGE_CAP, 1, Integer.MAX_VALUE, "Gaia Guardian damage cap (crits)");

            {
                configuration.addCustomCategoryComment("botania_capacities", "NOTE: One Mana Pool = 1000000 (1 million) mana.");
                Botania.ManaCapacities.ARCANE_ROSE = configuration.getInt("arcaneRose", "botania_capacities", Botania.ManaCapacities.ARCANE_ROSE, 1, Integer.MAX_VALUE, "Rosa Arcane mana capacity");
            } // Mana capacities

            {
                configuration.addCustomCategoryComment("botania_generation", "NOTE: 'Maximum' values displayed are NOT the true maximums!\nThe true maximum is the mana capacity set in the previous section.");
                Botania.GenerationAmounts.ARCANE_ROSE = configuration.getInt("arcaneRose", "botania_generation", Botania.GenerationAmounts.ARCANE_ROSE, 1, Botania.ManaCapacities.ARCANE_ROSE, "Rosa Arcana mana generation per experience point");
            } // Generation amounts
        } // Botania stuff
        {
            Thaumcraft.ELDRITCH_BOSS_DAMAGE_CAP = configuration.getInt("eldritchBossDamageCap", "thaumcraft", Thaumcraft.ELDRITCH_BOSS_DAMAGE_CAP, 1, Integer.MAX_VALUE, "Boss damage cap");
        } // Thaumcraft stuff

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
