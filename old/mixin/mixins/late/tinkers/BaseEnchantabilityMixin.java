package io.github.rpmyt.finetune.old.mixin.mixins.late.tinkers;

import io.github.rpmyt.finetune.old.FinetuneConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tconstruct.library.tools.ToolCore;

@Mixin(ToolCore.class)
public class BaseEnchantabilityMixin {
    @Inject(method = "getItemEnchantability", at = @At("HEAD"), cancellable = true)
    public void allowEnchanting(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(FinetuneConfig.Tinkers.TOOL_ENCHANTABILITY_BASE);
    }
}
