package io.github.rpmyt.finetune.mixin.plugin;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public enum Mixin {
    // LOTR
    COSMETIC_UNLOCKER("lotr.CosmeticUnlockerMixin", TargetedMod.LOTR),
    WAYPOINT_UNLOCKER("lotr.WaypointUnlockerMixin", TargetedMod.LOTR),
    MAKE_NPCS_NEUTRAL("lotr.NPCNeutralifierMixin", TargetedMod.LOTR),
    REMOVE_CUSTOM_COMBAT("lotr.CombatYeeterMixin", TargetedMod.LOTR),

    // Thaum
    CHANGE_ELDRITCH_DAMAGE_CAP("thaumcraft.EldritchBossDamageCapMixin", TargetedMod.THAUMCRAFT),

    // Botania
    ;

    public final String mixinClass;
    public final List<TargetedMod> targetedMods;
    private final Side side;

    Mixin(String mixinClass, Side side, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.side = side;
    }

    Mixin(String mixinClass, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.side = Side.BOTH;
    }

    public boolean shouldLoad(List<TargetedMod> loadedMods) {
        return (side == Side.BOTH
                || side == Side.SERVER && FMLLaunchHandler.side().isServer()
                || side == Side.CLIENT && FMLLaunchHandler.side().isClient())
                && new HashSet<>(loadedMods).containsAll(targetedMods);
    }
}

enum Side {
    BOTH,
    CLIENT,
    SERVER
}