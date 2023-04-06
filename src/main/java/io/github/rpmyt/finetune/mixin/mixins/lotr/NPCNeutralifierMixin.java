package io.github.rpmyt.finetune.mixin.mixins.lotr;

import io.github.rpmyt.finetune.FinetuneConfig;
import lotr.common.fac.LOTRFaction;
import lotr.common.fac.LOTRFactionRelations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LOTRFactionRelations.class)
public class NPCNeutralifierMixin {
    @Inject(method = "getRelations", at = @At("HEAD"), cancellable = true, remap = false)
    private static void neutralify(LOTRFaction f1, LOTRFaction f2, CallbackInfoReturnable<LOTRFactionRelations.Relation> cir) {
        if (FinetuneConfig.LOTR.MAKE_NPCS_NEUTRAL) {
            cir.setReturnValue(LOTRFactionRelations.Relation.NEUTRAL);
        }
    }
}
