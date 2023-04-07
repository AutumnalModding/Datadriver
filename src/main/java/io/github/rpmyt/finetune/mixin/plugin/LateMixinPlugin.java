package io.github.rpmyt.finetune.mixin.plugin;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import io.github.rpmyt.finetune.FinetuneInit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@com.gtnewhorizon.gtnhmixins.LateMixin
public class LateMixinPlugin implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.finetune.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        final List<String> mixins = new ArrayList<>();
        final List<String> notLoading = new ArrayList<>();

        ArrayList<String> mods = new ArrayList<>(loadedMods);
        for (LateMixin mixin : LateMixin.values()) {
            if (mixin.shouldLoad(mods)) {
                mixins.add(mixin.mixinClass);
            } else {
                notLoading.add(mixin.mixinClass);
            }
        }

        FinetuneInit.LOGGER.info("Not loading the following LATE mixins: {}", notLoading.toString());
        return mixins;
    }
}
