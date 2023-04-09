package io.github.rpmyt.datadriver;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import io.github.rpmyt.datadriver.util.data.DataItem;
import io.github.rpmyt.datadriver.util.data.Template;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

@Mod(modid = DatadriverInit.MODID, version = DatadriverInit.VERSION)
public class DatadriverInit {
    public static final String MODID = "datadriver";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger("Datadriver");

    private static final HashMap<ResourceLocation, DataItem> BUNDLES = new HashMap<>();
    private static final HashMap<ResourceLocation, Template> TEMPLATES = new HashMap<>();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File config = new File(event.getModConfigurationDirectory().getPath() + "/datadriver/");
        if (!config.exists()) {
            if (!config.mkdirs()) {
                LOGGER.fatal("Unable to create configuration directory!! Cannot proceed with initialization.");
                throw new IllegalStateException("DataDriver initialization failed!!");
            }
        }

        if (!config.isDirectory()) {
            LOGGER.fatal("DataDriver configuration directory... isn't a directory?!");
            throw new IllegalStateException("DataDriver initialization failed!!");
        }

        for (File pkg : config.listFiles()) {
            if (pkg.isDirectory()) {
                for (File contents : pkg.listFiles()) {
                    String ident = null;
                    if (contents.getName().equalsIgnoreCase("package.json")) {
                        Gson gson = new Gson();
                        try {
                            JsonReader reader = new JsonReader(new FileReader(contents));
                            reader.setLenient(true);
                            DataItem item = gson.fromJson(reader, DataItem.class);
                            BUNDLES.put(new ResourceLocation(item.identifier, "bundle_data"), item);
                            ident = item.identifier;
                            LOGGER.info("Loading bundle '" + item.name + "'!");
                        } catch (IOException | JsonSyntaxException exception) {
                            LOGGER.error("Failed to load package.json for bundle '" + pkg.getName() + "'!! Skipping it.");
                            continue;
                        }
                    } else if (contents.isDirectory()) {
                        for (File json : contents.listFiles()) {
                            switch (contents.getName()) {
                                case "templates": {
                                    Gson gson = new Gson();
                                    try {
                                        DataItem item = gson.fromJson(new JsonReader(new FileReader(json)), DataItem.class);
                                        if (item.type.equals("template")) {
                                            Template template = new Template(item);
                                            if (template.loaded) {
                                                TEMPLATES.put(new ResourceLocation(ident, template.identifier), template);
                                                LOGGER.info("Loaded template '" + ident + ":" + template.identifier + "'!");
                                            } else {
                                                LOGGER.error("Failed to load template " + ident + ":" + template.identifier + "'!");
                                            }
                                        }
                                    } catch (IOException exception) {
                                        exception.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //noinspection ConstantConditions
        // for (File bundle : config.listFiles((dir, name) -> name.contains(".json"))) {
        //     Gson gson = new Gson();
        //     try {
        //         Mundle mundle = gson.fromJson(new JsonReader(new FileReader(bundle)), Mundle.class);
        //         for (Mundle.Feature feature : mundle.features) {
        //             boolean skipping = false;
        //             for (String mod : feature.requiredMods) {
        //                 if (!Loader.isModLoaded(mod)) {
        //                     LOGGER.warn("Mundle feature '" + mundle.ident + ":" + feature.ident + "' requires mod '" + mod + "' but it is not loaded. Skipping feature.");
        //                     skipping = true;
        //                 }
        //             }
        //             if (!skipping) {
        //                 ArrayList<Class<?>> interfaces = new ArrayList<>();
        //                 Class<?> main = null;
        //                 for (String component : feature.uses) {
        //                     ResourceLocation location = new ResourceLocation(component.replaceAll("[@$].*", ""));
        //                     String identifier = component.replaceAll(location.toString(), "").replaceAll("[@$]", "");
        //                     ImmutablePair<ResourceLocation, String> pair = new ImmutablePair<>(location, identifier);

        //                     Class<?> clazz = COMPONENTS.get(pair);
        //                     if (clazz != null && clazz.isInterface()) {
        //                         interfaces.add(clazz);
        //                     } else {
        //                         main = clazz;
        //                     }
        //                 }

        //                 if (main == null) {
        //                     LOGGER.warn("No main component specified for feature '" + mundle.ident + ":" + feature.ident + "'. Skipping.");
        //                     continue;
        //                 }

        //                 switch (main.getSimpleName()) {
        //                     case "Aspect": {
        //                         String name = "Invalus";
        //                         Aspect[] components = null;
        //                         int colour = 0xFF0000;

        //                         for (String line : feature.contents) {
        //                             String value = line.replaceAll(".*=", "");
        //                             switch (line.replaceAll("=.*", "")) {
        //                                 case "ASPECT_NAME": {
        //                                     name = value;
        //                                     break;
        //                                 }

        //                                 case "ASPECT_COMPONENTS": {
        //                                     String[] aspects = value.split(",");
        //                                     components = new Aspect[aspects.length];
        //                                     for (int index = 0; index < aspects.length; index++) {
        //                                         if (Aspect.aspects.containsKey(aspects[index])) {
        //                                             components[index] = Aspect.getAspect(aspects[index]);
        //                                         } else {
        //                                             LOGGER.warn("Invalid aspect '" + aspects[index] + "'! Voidifying it.");
        //                                             components[index] = Aspect.VOID;
        //                                         }
        //                                     }
        //                                     break;
        //                                 }

        //                                 case "ASPECT_COLOR": {
        //                                     colour = Integer.parseInt(value, 16);
        //                                     break;
        //                                 }
        //                             }
        //                         }

        //                         LOGGER.info("Added new Thaumcraft aspect '" + name + "'!");
        //                         Aspect aspect = new Aspect(name, colour, components);
        //                         break;
        //                     }

        //                     case "ItemArmor": {
        //                         byte[] template = Launch.classLoader.getClassBytes("io.github.rpmyt.mundle.template.ArmourTemplate");
        //                         ClassNode node = new ClassNode(Opcodes.ASM5);
        //                         ClassReader reader = new ClassReader(template);
        //                         ClassWriter writer = new ClassWriter(0);
        //                         reader.accept(node, 0);

        //                         String material = "CLOTH";
        //                         String slot = "0";
        //                         String render = "3";
        //                         String runicShielding = "0";
        //                         String visDiscount = "0";
        //                         String manaDiscount = "0";
        //                         for (String line : feature.contents) {
        //                             String value = line.replaceAll(".*=", "");
        //                             switch (line.replaceAll("=.*", "")) {
        //                                 case "MATERIAL": {
        //                                     material = value;
        //                                     break;
        //                                 }

        //                                 case "SLOT": {
        //                                     slot = value;
        //                                     break;
        //                                 }

        //                                 case "RENDER": {
        //                                     render = value;
        //                                     break;
        //                                 }

        //                                 case "SHIELDING": {
        //                                     runicShielding = value;
        //                                     break;
        //                                 }

        //                                 case "VIS_DISCOUNT": {
        //                                     visDiscount = value;
        //                                     break;
        //                                 }

        //                                 case "MANA_DISCOUNT": {
        //                                     manaDiscount = value;
        //                                     break;
        //                                 }
        //                             }
        //                         }

        //                         ArrayList<String> toRemove = new ArrayList<>();

        //                         node.interfaces.iterator().forEachRemaining(iface -> {
        //                             boolean matches = false;
        //                             for (Class<?> clazz : interfaces) {
        //                                 if (clazz.getName().equals(iface)) {
        //                                     matches = true;
        //                                     break;
        //                                 }
        //                             }
        //                             if (!matches) {
        //                                 toRemove.add(iface);
        //                             }
        //                         });

        //                         for (String iface : toRemove) {
        //                             node.interfaces.remove(iface);
        //                         }

        //                         String __MATERIAL = material;
        //                         String __RENDER_INDEX = render;
        //                         String __SLOT = slot;
        //                         String __RUNIC_CHARGE = runicShielding;
        //                         String __VIS_DISCOUNT = visDiscount;
        //                         String __MANA_DISCOUNT = manaDiscount;
        //                         node.name = ("generated_" + String.valueOf((mundle.ident + ":" + feature.ident).hashCode()).replace("-", ""));
        //                         for (MethodNode method : node.methods) {
        //                             InsnList list = new InsnList();
        //                             if (method.name.contains("init")) {
        //                                 method.instructions.iterator().forEachRemaining(instruction -> {
        //                                     if (instruction.getOpcode() == Opcodes.LDC) {
        //                                         LdcInsnNode ldc = (LdcInsnNode) instruction;
        //                                         if (ldc.cst instanceof String) {
        //                                             switch ((String) ldc.cst) {
        //                                                 case "__MATERIAL": {
        //                                                     ldc = new LdcInsnNode(__MATERIAL);
        //                                                     break;
        //                                                 }

        //                                                 case "__RENDER_INDEX": {
        //                                                     ldc = new LdcInsnNode(__RENDER_INDEX);
        //                                                     break;
        //                                                 }

        //                                                 case "__SLOT": {
        //                                                     ldc = new LdcInsnNode(__SLOT);
        //                                                     break;
        //                                                 }
        //                                             }
        //                                             list.add(ldc);
        //                                         }
        //                                     } else {
        //                                         list.add(instruction);
        //                                     }
        //                                 });
        //                             }

        //                             switch (method.name) {
        //                                 case "getRunicCharge": {
        //                                     method.instructions.iterator().forEachRemaining(instruction -> {
        //                                         if (instruction.getOpcode() == Opcodes.LDC) {
        //                                             LdcInsnNode ldc = new LdcInsnNode(__RUNIC_CHARGE);
        //                                             list.add(ldc);
        //                                         } else {
        //                                             list.add(instruction);
        //                                         }
        //                                     });
        //                                     break;
        //                                 }

        //                                 case "getVisDiscount": {
        //                                     method.instructions.iterator().forEachRemaining(instruction -> {
        //                                         if (instruction.getOpcode() == Opcodes.LDC) {
        //                                             LdcInsnNode ldc = new LdcInsnNode(__VIS_DISCOUNT);
        //                                             list.add(ldc);
        //                                         } else {
        //                                             list.add(instruction);
        //                                         }
        //                                     });
        //                                     break;
        //                                 }

        //                                 case "getDiscount": {
        //                                     method.instructions.iterator().forEachRemaining(instruction -> {
        //                                         if (instruction.getOpcode() == Opcodes.LDC) {
        //                                             LdcInsnNode ldc = new LdcInsnNode(__MANA_DISCOUNT);
        //                                             list.add(ldc);
        //                                         } else {
        //                                             list.add(instruction);
        //                                         }
        //                                     });
        //                                     break;
        //                                 }
        //                             }

        //                             if (list.size() > 0) {
        //                                 method.instructions.clear();
        //                                 method.instructions.insert(list);
        //                             }
        //                         }
        //                         node.accept(writer);
        //                         File file = File.createTempFile("generated_class_", node.name.hashCode() + ".class");
        //                         try (FileOutputStream out = new FileOutputStream(file)) {
        //                             out.write(writer.toByteArray());
        //                             out.flush();
        //                             System.out.println("Dumped class file to " + file.getPath());
        //                         }

        //                         //noinspection unchecked
        //                         Class<ItemArmor> clazz = (Class<ItemArmor>) MundleClassLoader.INSTANCE.load(node.name, writer.toByteArray());

        //                         try {
        //                             Constructor<ItemArmor> constructor = clazz.getDeclaredConstructor();

        //                             ItemArmor instance = constructor.newInstance();
        //                             GameRegistry.registerItem(instance, feature.ident);
        //                         } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
        //                             LOGGER.error("Unable to load feature '" + mundle.ident + ":" + feature.ident + "'!");
        //                             exception.printStackTrace();
        //                         }
        //                     }
        //                 }
        //             }
        //         }
        //     } catch (IOException exception) {
        //         LOGGER.warn("Failed to read Mundle file '" + bundle.getName() + "'. Skipping it.");
        //     }
        // }
    }
}
