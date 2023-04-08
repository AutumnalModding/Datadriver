package io.github.rpmyt.finetune.old.mixin.mixins.late.botania;

import io.github.rpmyt.finetune.old.FinetuneConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import vazkii.botania.common.entity.EntityDoppleganger;

@Mixin(EntityDoppleganger.class)
public class GaiaDamageCapMixin {
    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(intValue = 40))
    private int modifyDamageCap(int damage) {
        return FinetuneConfig.Botania.GAIA_DAMAGE_CAP;
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(intValue = 60))
    private int modifyDamageCapCrit(int damage) {
        return FinetuneConfig.Botania.GAIA_DAMAGE_CAP_CRIT;
    }
}
