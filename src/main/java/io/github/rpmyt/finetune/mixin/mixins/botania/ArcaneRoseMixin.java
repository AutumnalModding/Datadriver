package io.github.rpmyt.finetune.mixin.mixins.botania;

import io.github.rpmyt.finetune.FinetuneConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.block.subtile.generating.SubTileArcaneRose;

@Mixin(SubTileArcaneRose.class)
public class ArcaneRoseMixin {
    @Inject(method = "getMaxMana", at = @At("HEAD"), cancellable = true, remap = false)
    public void modify(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(FinetuneConfig.Botania.ManaCapacities.ARCANE_ROSE);
    }

    @ModifyConstant(method = "onUpdate", constant = @Constant(intValue = 50), remap = false)
    private int modify(int constant) {
        return FinetuneConfig.Botania.GenerationAmounts.ARCANE_ROSE;
    }
}
