package io.github.rpmyt.finetune.old.mixin.mixins.thaumcraft.aspect;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigAspects;

@Mixin(ConfigAspects.class)
public class ItemAspectRegistryMixin {
    @Inject(method = "registerItemAspects", at = @At("TAIL"), remap = false)
    private static void registerAdditional(CallbackInfo ci) {

    }
}
