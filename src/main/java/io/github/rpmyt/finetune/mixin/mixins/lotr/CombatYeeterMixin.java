package io.github.rpmyt.finetune.mixin.mixins.lotr;

import io.github.rpmyt.finetune.FinetuneConfig;
import lotr.common.item.LOTRWeaponStats;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LOTRWeaponStats.class)
public class CombatYeeterMixin {
    @Inject(method = "getMeleeSpeed", at = @At("HEAD"), cancellable = true, remap = false)
    private static void yeetify(ItemStack itemstack, CallbackInfoReturnable<Float> cir) {
        if (FinetuneConfig.LOTR.REMOVE_COMBAT) {
            cir.setReturnValue(1.0F);
        }
    }
}
