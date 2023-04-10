package io.github.rpmyt.datadriver.util;

import net.minecraft.item.ItemArmor;

import java.util.HashMap;

public class ArmourMaterials {
    public static final HashMap<String, ItemArmor.ArmorMaterial> MATERIALS = new HashMap<>();

    public static ItemArmor.ArmorMaterial get(String name) {
        try {
            return ItemArmor.ArmorMaterial.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return MATERIALS.get(name);
        }
    }

    public static void add(ItemArmor.ArmorMaterial material, String name) {
        System.out.println("Added material '" + name + "'.");
        MATERIALS.put(name, material);
    }
}
