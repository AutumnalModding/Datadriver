package io.github.rpmyt.mundle.template;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;
import vazkii.botania.api.mana.IManaDiscountArmor;
import vazkii.botania.api.mana.IManaUsingItem;

@Optional.InterfaceList({
        @Optional.Interface(iface = "thaumcraft.api.IRunicArmor", modid = "Thaumcraft"),
        @Optional.Interface(iface = "thaumcraft.api.IVisDiscountGear", modid = "Thaumcraft"),
        @Optional.Interface(iface = "vazkii.botania.api.mana.IManaUsingItem", modid = "Botania"),
        @Optional.Interface(iface = "vazkii.botania.api.mana.IManaDiscountArmor", modid = "Botania"),
})
@SuppressWarnings("unused")
public class ArmourTemplate extends ItemArmor implements IRunicArmor, IVisDiscountGear, IManaUsingItem, IManaDiscountArmor {

    public ArmourTemplate() {
        super(ArmorMaterial.valueOf("__MATERIAL"), Integer.parseInt("__RENDER_INDEX"), Integer.parseInt("__SLOT"));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
//                return slot == 3 ? "__REPLACEME_04" : "__REPLACEME_05";
        return slot == 3 ? "minecraft:textures/models/armor/diamond_layer_2.png" : "minecraft:textures/models/armor/diamond_layer_1.png";
    }

    @Optional.Method(modid = "Thaumcraft")
    public int getRunicCharge(ItemStack itemStack) {
        return Integer.parseInt("__RUNIC_CHARGE");
    }

    @Optional.Method(modid = "Thaumcraft")
    public int getVisDiscount(ItemStack itemStack, EntityPlayer entityPlayer, Aspect aspect) {
        return Integer.parseInt("__VIS_DISCOUNT");
    }

    @Optional.Method(modid = "Botania")
    public boolean usesMana(ItemStack itemStack) {
        return true;
    }

    @Optional.Method(modid = "Botania")
    public float getDiscount(ItemStack itemStack, int i, EntityPlayer entityPlayer) {
        return Float.parseFloat("__MANA_DISCOUNT");
    }
}
