package io.github.rpmyt.mundle.util;

import java.util.HashMap;

public class MundleClassLoader extends ClassLoader {
    private static final HashMap<String, Class<?>> LOADED = new HashMap<>();

    public static final MundleClassLoader INSTANCE = new MundleClassLoader(getSystemClassLoader());

    protected MundleClassLoader(ClassLoader parent) {
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
