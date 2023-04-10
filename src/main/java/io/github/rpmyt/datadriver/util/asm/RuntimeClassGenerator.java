package io.github.rpmyt.datadriver.util.asm;

import io.github.rpmyt.datadriver.util.DataClassLoader;
import io.github.rpmyt.datadriver.util.data.ObjectData;
import io.github.rpmyt.datadriver.util.data.TemplateData;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RuntimeClassGenerator {
    public static Class<?> generate(ObjectData object, String identifier, boolean save) throws IOException {
        class Generated {}
        Class<Generated> clazz = Generated.class;

        ClassNode node = new ClassNode(Opcodes.ASM5);
        ClassReader reader = new ClassReader(Launch.classLoader.getClassBytes(clazz.getName()));
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        reader.accept(node, 0);
        node.methods.remove(0);
        node.name = "GeneratedClass_" + String.valueOf((identifier + ":" + object.identifier).hashCode()).replaceAll("-", "");
        node.superName = object.main.superclass.getName().replaceAll("\\.", "/");
        object.main.methods.forEach((desc, bytecode) -> {
            InsnList list = BytecodeParser.parse(bytecode, object.replacements, desc, object.main, object.identifier);
            MethodNode method = new MethodNode();
            method.instructions = list;
            method.name = desc.replaceAll(".*\\.", "").replaceAll("\\(.*", "");
            method.desc = desc.replaceAll(".*\\(", "(");
            method.signature = desc;
            method.exceptions = new ArrayList<>();
            method.access = Opcodes.ACC_PUBLIC;
            node.methods.add(method);
        });

        for (TemplateData template : object.extensions) {
            node.interfaces.add(template.superclass.getName().replaceAll("\\.", "/"));
            template.methods.forEach((desc, bytecode) -> {
                InsnList list = BytecodeParser.parse(bytecode, object.replacements, desc, template, object.identifier);
                MethodNode method = new MethodNode();
                method.instructions = list;
                method.name = desc.replaceAll(".*\\.", "").replaceAll("\\(.*", "");
                method.desc = desc.replaceAll(".*\\(", "(");
                method.signature = desc;
                method.exceptions = new ArrayList<>();
                method.access = Opcodes.ACC_PUBLIC;
                node.methods.add(method);
            });
        }

        node.access = Opcodes.ACC_PUBLIC;
        node.accept(writer);
        if (save) {
            try (FileOutputStream out = new FileOutputStream(File.createTempFile("generated_", ".class"))) {
                out.write(writer.toByteArray());
                out.flush();
            }
        }

        return DataClassLoader.INSTANCE.load(node.name, writer.toByteArray());
    }
}
