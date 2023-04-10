package io.github.rpmyt.datadriver.util.asm;

import com.google.gson.JsonPrimitive;
import io.github.rpmyt.datadriver.DatadriverInit;
import io.github.rpmyt.datadriver.util.data.TemplateData;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class BytecodeParser {
    public static InsnList parse(ArrayList<String> bytecode, HashMap<String, JsonPrimitive> replacements, String desc, TemplateData template, String name) {
        InsnList list = new InsnList();
        for (String insn : bytecode) {
            String method = insn.replaceAll(" .*", "");
            String argument = insn.replaceAll(".* ", "");

            Class<Opcodes> opcodes = Opcodes.class;
            try {
                Field field = opcodes.getField(name);
                Object value = field.get(null);
                if (value instanceof Integer) {
                    int code = (int) value;
                    switch (code) {
                        case Opcodes.LDC: {
                            template.placeholders.forEach((place, holder) -> {
                                String target = desc
                                        .replaceAll(".*\\.", "")
                                        .replaceAll("\\(.*", "");
                                
                                String type = place.replaceAll(".*#", "");
                                if (replacements.containsKey(holder) && place.contains(target) && argument.equals(place
                                        .replace(target + "$", "")
                                        .replace("#" + type, "")
                                )) {
                                    JsonPrimitive primitive = replacements.get(holder);
                                    DatadriverInit.LOGGER.debug("Replacing " + place + " with " + primitive.toString());
                                    switch (type) {
                                        case "int": {
                                            list.add(new LdcInsnNode(primitive.getAsInt()));
                                            break;
                                        }

                                        case "float": {
                                            list.add(new LdcInsnNode(primitive.getAsFloat()));
                                            break;
                                        }

                                        case "double": {
                                            list.add(new LdcInsnNode(primitive.getAsDouble()));
                                            break;
                                        }

                                        case "long": {
                                            list.add(new LdcInsnNode(primitive.getAsLong()));
                                            break;
                                        }

                                        case "string": {
                                            list.add(new LdcInsnNode(primitive.getAsString()));
                                            break;
                                        }
                                    }
                                }
                            });

                            if (argument.equals("__NAME")) {
                                list.add(new LdcInsnNode(name));
                            }

                            if ((list.getLast() != null && list.getLast().getClass() != LdcInsnNode.class) || list.getLast() == null) {
                                list.add(new LdcInsnNode(argument));
                            }

                            break;
                        }

                        case Opcodes.INVOKESPECIAL:
                        case Opcodes.INVOKESTATIC:
                        case Opcodes.INVOKEVIRTUAL:
                        case Opcodes.INVOKEINTERFACE: {
                            String target = argument
                                    .replaceAll(".*\\.", "")
                                    .replaceAll("\\(.*", "");
                            String owner = argument.replaceAll("\\..*", "");
                            String description = argument.replaceAll(".*\\(", "(");
                            list.add(new MethodInsnNode(code, owner, target, description, template.superclass.isInterface()));
                            break;
                        }

                        case Opcodes.ALOAD:
                        case Opcodes.ILOAD:
                        case Opcodes.LLOAD:
                        case Opcodes.DLOAD:
                        case Opcodes.FLOAD:
                        case Opcodes.ASTORE:
                        case Opcodes.ISTORE:
                        case Opcodes.LSTORE:
                        case Opcodes.DSTORE:
                        case Opcodes.FSTORE:
                        case Opcodes.RET: {
                            list.add(new VarInsnNode(code, Integer.parseInt(argument)));
                            break;
                        }

                        case Opcodes.NEWARRAY: {
                            switch (argument) {
                                case "I": {
                                    list.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_INT));
                                    break;
                                }

                                case "B": {
                                    list.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_BYTE));
                                    break;
                                }

                                case "C": {
                                    list.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_CHAR));
                                    break;
                                }

                                case "F": {
                                    list.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_FLOAT));
                                    break;
                                }

                                case "D": {
                                    list.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_DOUBLE));
                                    break;
                                }

                                case "J": {
                                    list.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_LONG));
                                    break;
                                }

                                case "S": {
                                    list.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_SHORT));
                                    break;
                                }

                                case "Z": {
                                    list.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_BOOLEAN));
                                    break;
                                }
                            }
                            break;
                        }

                        case Opcodes.BIPUSH:
                        case Opcodes.SIPUSH: {
                            list.add(new IntInsnNode(code, Integer.parseInt(argument)));
                            break;
                        }

                        case Opcodes.ANEWARRAY:
                        case Opcodes.INSTANCEOF:
                        case Opcodes.CHECKCAST:
                        case Opcodes.NEW: {
                            list.add(new TypeInsnNode(code, desc));
                        }

                        default: {
                            list.add(new InsnNode(code));
                        }
                    }
                } else {
                    // What the fuck?
                    DatadriverInit.LOGGER.fatal("Something has gone very wrong...");
                    DatadriverInit.LOGGER.fatal("Opcodes." + field + " isn't an int???");
                }
            } catch (NoSuchFieldException exception) {
                DatadriverInit.LOGGER.fatal("Invalid opcode '" + name + "'!!");
                DatadriverInit.LOGGER.fatal("=== CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL ===");
                DatadriverInit.LOGGER.fatal("ENTERING INVALID BYTECODE WATERS! ABANDON SHIP, ALL HOPE IS LOST!");
                DatadriverInit.LOGGER.fatal("(The game is HIGHLY LIKELY to crash from this point on. YOU'VE BEEN WARNED!");
                DatadriverInit.LOGGER.fatal("=== CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL CRITICAL ===");
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }

        return list;
    }
}
