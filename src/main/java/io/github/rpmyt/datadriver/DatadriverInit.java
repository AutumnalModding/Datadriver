package io.github.rpmyt.datadriver;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import io.github.rpmyt.datadriver.util.data.GenericData;
import io.github.rpmyt.datadriver.util.data.ItemData;
import io.github.rpmyt.datadriver.util.data.TemplateData;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@Mod(modid = DatadriverInit.MODID, version = DatadriverInit.VERSION)
public class DatadriverInit {
    public static final String MODID = "datadriver";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger("Datadriver");

    private static final HashMap<ResourceLocation, GenericData> BUNDLES = new HashMap<>();
    public static final HashMap<ResourceLocation, TemplateData> TEMPLATES = new HashMap<>();

    public static final String[] LOAD_ORDER;

    @EventHandler
    @SuppressWarnings("ConstantConditions")
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

        Gson gson = new Gson();
        for (File pkg : config.listFiles()) {
            if (pkg.isDirectory()) {
                for (File contents : pkg.listFiles()) {
                    String ident = null;
                    if (contents.getName().equalsIgnoreCase("package.json")) {
                        try {
                            JsonReader reader = new JsonReader(new FileReader(contents));
                            reader.setLenient(true);
                            GenericData item = gson.fromJson(reader, GenericData.class);
                            BUNDLES.put(new ResourceLocation(item.identifier, "bundle_data"), item);
                            ident = item.identifier;
                            LOGGER.info("Loading bundle '" + item.name + "'!");
                        } catch (IOException | JsonSyntaxException exception) {
                            LOGGER.error("Failed to load package.json for bundle '" + pkg.getName() + "'!! Skipping it.");
                            continue;
                        }
                    }

                    for (String current : LOAD_ORDER) {
                        File directory = new File(pkg.toPath() + "/" + current);
                        if (directory.exists() && directory.isDirectory()) {
                            for (File json : directory.listFiles()) {
                                switch (current) {
                                    case "templates": {
                                        try {
                                            TemplateData template = gson.fromJson(new JsonReader(new FileReader(json)), GenericData.class);
                                            template.init();
                                            if (template.type.equals("template")) {
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
                                        break;
                                    }

                                    case "items": {
                                        try {
                                            ItemData item = gson.fromJson(new JsonReader(new FileReader(json)), GenericData.class);
                                            item.init();
                                            if (item.type.equals("item")) {
                                                if (item.loaded) {
                                                    class GeneratedItem extends Object {}
                                                    Class<GeneratedItem> clazz = GeneratedItem.class;

                                                    ClassNode node = new ClassNode(Opcodes.ASM5);
                                                    ClassReader reader = new ClassReader(Launch.classLoader.getClassBytes(clazz.getName()));
                                                    ClassWriter writer = new ClassWriter(0);

                                                    reader.accept(node, 0);
                                                    node.superName = item.main.superclass.getName().replaceAll("\\.", "/");
                                                    item.main.methods.forEach((desc, bytecode) -> {
                                                        InsnList list = new InsnList();
                                                        for (String insn : bytecode) {
                                                            String name = insn.replaceAll(" .*", "");
                                                            String argument = insn.replaceAll(".* ", "");

                                                            Class<Opcodes> opcodes = Opcodes.class;
                                                            try {
                                                                Field field = opcodes.getField(name);
                                                                Object value = field.get(null);
                                                                if (value instanceof Integer) {
                                                                    int code = (int) value;
                                                                    AbstractInsnNode instruction;
                                                                    switch (code) {
                                                                        case Opcodes.LDC: {
                                                                            instruction = new LdcInsnNode(argument);
                                                                            break;
                                                                        }

                                                                        case Opcodes.INVOKESPECIAL:
                                                                        case Opcodes.INVOKESTATIC:
                                                                        case Opcodes.INVOKEVIRTUAL:
                                                                        case Opcodes.INVOKEINTERFACE: {
                                                                            /*
                                                                            Example:
                                                                            ItemArmor#getArmorTexture(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/Entity;ILjava/lang/String;)Ljava/lang/String;
                                                                             */
                                                                            String method = argument.replaceAll(".*\\.", "").replaceAll("\\(.*", "");
                                                                            String owner = argument.replaceAll("\\..*", "");
                                                                            String description = argument.replaceAll(".*\\(", "(");
                                                                            instruction = new MethodInsnNode(code, owner, method, description, item.main.superclass.isInterface());
                                                                            break;
                                                                        }
                                                                        
                                                                        case Opcodes.ALOAD:
                                                                        case Opcodes.ILOAD:
                                                                        case Opcodes.LLOAD:
                                                                        case Opcodes.DLOAD:
                                                                        case Opcodes.FLOAD:
                                                                        case Opcodes.ASTORE:
                                                                        case Opcodes.ISTORE:
                                                                        case Opcodes.LSTORE:
                                                                        case Opcodes.DSTORE:
                                                                        case Opcodes.FSTORE:
                                                                        case Opcodes.RET: {
                                                                            instruction = new VarInsnNode(code, Integer.parseInt(argument));
                                                                            break;
                                                                        }

                                                                        default: {
                                                                            instruction = new InsnNode(code);
                                                                        }
                                                                    }
                                                                    list.add(instruction);
                                                                } else {
                                                                    // What the fuck?
                                                                    LOGGER.fatal("Something has gone very wrong...");
                                                                    LOGGER.fatal("Opcodes." + field + " isn't an int???");
                                                                }
                                                            } catch (NoSuchFieldException exception) {
                                                                LOGGER.fatal("Invalid opcode '" + name + "'!!");
                                                                LOGGER.fatal("=== CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL ===");
                                                                LOGGER.fatal("ENTERING INVALID BYTECODE WATERS! ABANDON SHIP, ALL HOPE IS LOST!");
                                                                LOGGER.fatal("(The game is HIGHLY LIKELY to crash from this point on. YOU'VE BEEN WARNED!");
                                                                LOGGER.fatal("=== CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL ===");
                                                            } catch (IllegalAccessException exception) {
                                                                exception.printStackTrace();
                                                            }
                                                        }
                                                    });

                                                    for (TemplateData template : item.extensions) {
                                                        node.interfaces.add(template.superclass.getName().replaceAll("\\.", "/"));
                                                        template.methods.forEach((desc, bytecode) -> {

                                                        });
                                                    }

                                                    node.accept(writer);
                                                    try (FileOutputStream out = new FileOutputStream(File.createTempFile("generated_", String.valueOf(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE))))) {
                                                        out.write(writer.toByteArray());
                                                        out.flush();
                                                    }
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

    static {
        LOAD_ORDER = new String[11];

        LOAD_ORDER[0] = "templates";
        LOAD_ORDER[1] = "models";
        LOAD_ORDER[2] = "items";
        LOAD_ORDER[3] = "blocks";
        LOAD_ORDER[4] = "materials";
        LOAD_ORDER[5] = "armor";
        LOAD_ORDER[6] = "tools";
        LOAD_ORDER[7] = "potions";
        LOAD_ORDER[8] = "entities";
        LOAD_ORDER[9] = "recipes";
        LOAD_ORDER[10] = "achievements";
    }
}
