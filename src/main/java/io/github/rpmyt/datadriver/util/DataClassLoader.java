package io.github.rpmyt.datadriver.util;

import net.minecraft.launchwrapper.Launch;

import java.util.HashMap;

public class DataClassLoader extends ClassLoader {
    private static final HashMap<String, Class<?>> LOADED = new HashMap<>();
    public static final DataClassLoader INSTANCE = new DataClassLoader(Launch.classLoader);

    private DataClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> load(String name, byte[] bytes) {
        if (LOADED.containsKey(name)) {
            return LOADED.get(name);
        }

        Class<?> clazz = this.defineClass(name, bytes, 0, bytes.length);
        this.resolveClass(clazz);
        LOADED.put(name, clazz);
        return clazz;
    }
}
