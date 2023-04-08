package io.github.rpmyt.mundle.template;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ArmourTemplate extends ItemArmor {

    public ArmourTemplate() {
        super(ArmorMaterial.valueOf("__REPLACEME_01"), Integer.parseInt("__REPLACEME_02"), Integer.parseInt("__REPLACEME_03"));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
//                return slot == 3 ? "__REPLACEME_04" : "__REPLACEME_05";
        return slot == 3 ? "minecraft:textures/models/armor/diamond_layer_2.png" : "minecraft:textures/models/armor/diamond_layer_1.png";
    }
}
