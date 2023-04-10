package io.github.rpmyt.datadriver.util.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.rpmyt.datadriver.DatadriverInit;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ObjectData extends GenericData {
    public TemplateData main;
    public ArrayList<TemplateData> extensions = new ArrayList<>();
    public HashMap<String, JsonPrimitive> replacements = new HashMap<>();

    public ObjectData(GenericData parent) {
        this.data = parent.data;
        this.name = parent.name;
        this.type = parent.type;
        this.identifier = parent.identifier;
        this.init();
    }

    public void init() {
        for (Map.Entry<String, JsonElement> entry : this.data.entrySet()) {
            switch (entry.getKey()) {
                case "template": {
                    if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString()) {
                        TemplateData template = DatadriverInit.TEMPLATES.get(new ResourceLocation(entry.getValue().getAsJsonPrimitive().getAsString()));
                        if (template == null) {
                            this.loaded = false;
                            return;
                        }
                        this.main = template;
                    }
                    break;
                }

                case "extensions": {
                    if (entry.getValue().isJsonArray()) {
                        for (JsonElement element : entry.getValue().getAsJsonArray()) {
                            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                                TemplateData template = DatadriverInit.TEMPLATES.get(new ResourceLocation(entry.getValue().getAsJsonPrimitive().getAsString()));
                                if (template == null) {
                                    this.loaded = false;
                                    return;
                                }
                                this.extensions.add(template);
                            }
                        }
                    }
                    break;
                }

                case "replacements": {
                    if (entry.getValue().isJsonObject()) {
                        entry.getValue().getAsJsonObject().entrySet().forEach(replacement -> {
                            if (replacement.getValue().isJsonPrimitive()) {
                                replacements.put(replacement.getKey(), replacement.getValue().getAsJsonPrimitive());
                            }
                        });
                    }
                    break;
                }
            }
        }

        this.loaded = true;
    }
}
