package io.github.rpmyt.finetune.core;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1002)
@IFMLLoadingPlugin.TransformerExclusions("io.github.rpmyt.opinionation.core")
public class FinetuneCore implements IFMLLoadingPlugin {
    static final Logger LOGGER = LogManager.getLogger("Finetune Core");
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ASMTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    static {
        LaunchClassLoader loader = Launch.classLoader;
        try {
            Field field = loader.getClass().getDeclaredField("transformerExceptions");
            field.setAccessible(true);
            Object obj = field.get(loader);
            if (obj instanceof Set) {
                //noinspection rawtypes
                Set set = (Set) obj;
                set.remove("lotr.common.coremod");
            }

            loader.registerTransformer("io.github.rpmyt.finetune.core.ASMTransformer");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

