package io.github.rpmyt.finetune.old.mixin.plugin;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public enum LateMixin {
    ARCANE_ROSE("botania.flower.ArcaneRoseMixin", TargetedMod.BOTANIA),
    CHANGE_GAIA_DAMAGE_CAP("botania.GaiaDamageCapMixin", TargetedMod.BOTANIA),
    ALLOW_TOOL_ENCHANTING("tinkers.BaseEnchantabilityMixin", TargetedMod.THAUMCRAFT)

    ;

    public final String mixinClass;
    public final List<String> targets;
    private final Side side;

    LateMixin(String mixinClass, Side side, TargetedMod... targets) {
        this.mixinClass = mixinClass;
        this.targets = new ArrayList<>();
        for (TargetedMod target : targets) {
            this.targets.add(target.modID);
        }
        this.side = side;
    }

    LateMixin(String mixinClass, TargetedMod... targets) {
        this.mixinClass = mixinClass;
        this.targets = new ArrayList<>();
        for (TargetedMod target : targets) {
            this.targets.add(target.modID);
        }
        this.side = Side.BOTH;
    }

    public boolean shouldLoad(List<String> loadedMods) {
        return (side == Side.BOTH
                || side == Side.SERVER && FMLLaunchHandler.side().isServer()
                || side == Side.CLIENT && FMLLaunchHandler.side().isClient())
                && new HashSet<>(loadedMods).containsAll(targets);
    }
}
