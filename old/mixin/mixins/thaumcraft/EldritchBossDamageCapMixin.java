package io.github.rpmyt.finetune.old.mixin.mixins.thaumcraft;

import io.github.rpmyt.finetune.old.FinetuneConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;

@Mixin(EntityThaumcraftBoss.class)
public class EldritchBossDamageCapMixin {
    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 35.0F, ordinal = 0))
    private float changeDamageCap(float cap) {
        return FinetuneConfig.Thaumcraft.ELDRITCH_BOSS_DAMAGE_CAP;
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 35.0F, ordinal = 1))
    private float changeDamageReduction(float damage) {
        return FinetuneConfig.Thaumcraft.ELDRITCH_BOSS_DAMAGE_CAP;
    }

    @Inject(method = "getAnger", at = @At("HEAD"), cancellable = true, remap = false)
    public void nullifyAnger(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(-1);
    }
}