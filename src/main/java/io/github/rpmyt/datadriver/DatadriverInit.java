package io.github.rpmyt.datadriver;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import io.github.rpmyt.datadriver.util.asm.RuntimeClassGenerator;
import io.github.rpmyt.datadriver.util.data.GenericData;
import io.github.rpmyt.datadriver.util.data.ObjectData;
import io.github.rpmyt.datadriver.util.data.TemplateData;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

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
            String ident = null;
            if (pkg.isDirectory()) {
                for (File contents : pkg.listFiles()) {
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
                        }
                    }
                }

                for (String current : LOAD_ORDER) {
                    File directory = new File(pkg.toPath() + "/" + current);
                    if (directory.exists() && directory.isDirectory()) {
                        for (File json : directory.listFiles()) {
                            if (current.equals("templates")) {
                                try {
                                    GenericData generic = gson.fromJson(new JsonReader(new FileReader(json)), GenericData.class);
                                    TemplateData template = new TemplateData(generic);
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
                            } else {
                                try {
                                    GenericData generic = gson.fromJson(new JsonReader(new FileReader(json)), GenericData.class);
                                    ObjectData object = new ObjectData(generic);
                                    if (object.loaded) {
                                        Class<?> clazz = RuntimeClassGenerator.generate(object, ident, false);
                                        try {
                                            Constructor<?> constructor = clazz.getConstructor();
                                            Object obj = constructor.newInstance();
                                            switch (object.type) {
                                                case "item":
                                                case "tool":
                                                case "armor": {
                                                    GameRegistry.registerItem((Item) obj, ident + "$" + object.identifier);
                                                    break;
                                                }

                                                case "block": {
                                                    GameRegistry.registerBlock((Block) obj, ident + "$" + object.identifier);
                                                    break;
                                                }
                                            }
                                        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                                            throw new RuntimeException(e);
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
