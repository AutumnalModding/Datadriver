package io.github.rpmyt.datadriver.util.data;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import io.github.rpmyt.datadriver.DatadriverInit;

import java.util.Map.Entry;

public class TemplateData extends GenericData {
    public Class<?> superclass;
    public HashMap<String, ArrayList<String>> methods = new HashMap<>();
    public ArrayList<String> requiredMods = new ArrayList<>();
    public HashMap<String, String> placeholders = new HashMap<>();

    public TemplateData(GenericData parent) {
        this.data = parent.data;
        this.name = parent.name;
        this.type = parent.type;
        this.identifier = parent.identifier;
        this.init();
    }

    public void init() {
        for (Entry<String,JsonElement> entry : this.data.entrySet()) {
            switch (entry.getKey()) {
                case "mods": {
                    if (entry.getValue().isJsonArray()) {
                        JsonArray modlist = entry.getValue().getAsJsonArray();
                        modlist.forEach(mod -> {
                            if (mod.isJsonPrimitive() && mod.getAsJsonPrimitive().isString()) {
                                requiredMods.add(mod.getAsJsonPrimitive().getAsString());
                            }
                        });
                    }
                    break;
                }

                case "class": {
                    if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString()) {
                        String desc = entry.getValue().getAsJsonPrimitive().getAsString();
                        try {
                            this.superclass = Class.forName(desc);
                        } catch (ClassNotFoundException exception) {
                            DatadriverInit.LOGGER.error("Unable to load superclass '" + desc + "'!");
                            this.loaded = false;
                            return;
                        }
                    }
                    break;
                }

                case "methods": {
                    if (entry.getValue().isJsonObject()) {
                        entry.getValue().getAsJsonObject().entrySet().forEach(method -> {
                            if (method.getValue().isJsonArray()) {
                                ArrayList<String> bytecode = new ArrayList<>();
                                method.getValue().getAsJsonArray().forEach(instruction -> {
                                    if (instruction.isJsonPrimitive() && instruction.getAsJsonPrimitive().isString()) {
                                        bytecode.add(instruction.getAsJsonPrimitive().getAsString());
                                    }
                                });
                                methods.put(entry.getKey(), bytecode);
                            }
                        });
                    }
                    break;
                }

                case "placeholders": {
                    if (entry.getValue().isJsonObject()) {
                        entry.getValue().getAsJsonObject().entrySet().forEach(placeholder -> {
                            if (placeholder.getValue().isJsonPrimitive() && placeholder.getValue().getAsJsonPrimitive().isString()) {
                                placeholders.put(entry.getKey(), placeholder.getValue().getAsJsonPrimitive().getAsString());
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