package io.github.rpmyt.finetune.core;

import cpw.mods.fml.common.Loader;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ASMTransformer implements IClassTransformer {
    public ASMTransformer() {}

    @Override
    public byte[] transform(String name, String transformed, byte[] bytes) {
        ClassNode node = new ClassNode(Opcodes.ASM5);
        ClassReader reader = new ClassReader(bytes);
        ClassWriter writer = new ClassWriter(0);
        reader.accept(node, 0);

        AtomicBoolean stripped = new AtomicBoolean(false);
        for (MethodNode method : node.methods) {
            if (method.visibleAnnotations != null) {
                for (AnnotationNode annotation : method.visibleAnnotations) {
                    if (annotation.desc.equals("Lio/github/rpmyt/finetune/util/RequiresMod;")) {
                        List<Object> values = annotation.values;
                        for (Object value : values) {
                            if (value instanceof String && !value.equals("value")) {
                                String modID = (String) value;
                                if (!Loader.isModLoaded(modID)) {
                                    FinetuneCore.LOGGER.debug("Stripping method '" + method.name + "' from class '" + node.name + "' because required mod '" + modID + "' is not loaded.");
                                    method.instructions.insert(new InsnNode(Opcodes.RETURN));
                                    node.accept(writer);
                                    stripped.set(true);
                                }
                            }
                        }
                    }
                }
            }
        }

        return stripped.get() ? writer.toByteArray() : bytes;
    }
}