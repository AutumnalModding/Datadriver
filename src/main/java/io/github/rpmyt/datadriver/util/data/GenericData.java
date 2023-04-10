package io.github.rpmyt.datadriver.util.data;

import com.google.gson.JsonObject;

public class GenericData {
    public String name;
    public String identifier;
    public String type;
    public JsonObject data;

    public boolean loaded;

    public GenericData() {
        this.loaded = false;
    }
}