package kr.starly.libs.nms.reflect.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.*;
import java.security.PrivilegedAction;

@SuppressWarnings({"rawtypes", "removal"})
public abstract class AccessUtil {

    private static final Object modifiersVarHandle;
    private static final Field modifiersField;


    public static Field setAccessible(Field field) {
        return setAccessible(field, false);
    }

    public static Field setAccessible(Field field, boolean readOnly) {
        return setAccessible(field, readOnly, false);
    }

    private static Field setAccessible(Field field, boolean readOnly, boolean privileged) {
        try {
            field.setAccessible(true);
        } catch (InaccessibleObjectException ignored1) {
            if (!privileged) {
                return java.security.AccessController.doPrivileged((PrivilegedAction<Field>) () -> {
                    try {
                        return setAccessible(field, readOnly, true);
                    } catch (Exception ignored2) {
                    }

                    return field;
                });
            }
        }

        if (readOnly) return field;

        removeFinal(field, privileged);
        return field;
    }

    private static void removeFinal(Field field, boolean privileged) {
        int modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            try {
                removeFinalSimple(field);
            } catch (Exception ignored1) {
                try {
                    removeFinalVarHandle(field);
                } catch (Exception ignored2) {
                    try {
                        removeFinalNativeDeclaredFields(field);
                    } catch (Exception ignored3) {
                        if (!privileged) {
                            java.security.AccessController.doPrivileged((PrivilegedAction<Field>) () -> {
                                try {
                                    setAccessible(field, false, true);
                                } catch (Exception ignored4) {
                                }

                                return null;
                            });

                            return;
                        }
                    }
                }
            }
        }
    }

    private static void removeFinalSimple(Field field) throws ReflectiveOperationException {
        int modifiers = field.getModifiers();

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
    }

    private static void removeFinalVarHandle(Field field) throws ReflectiveOperationException {
        int modifiers = field.getModifiers();
        int newModifiers = modifiers & ~Modifier.FINAL;

        if (modifiersVarHandle != null) {
            ((VarHandle) modifiersVarHandle).set(field, newModifiers);
        } else {
            modifiersField.setInt(field, newModifiers);
        }
    }

    private static void removeFinalNativeDeclaredFields(Field field) throws ReflectiveOperationException {
        int modifiers = field.getModifiers();

        // https://github.com/ViaVersion/ViaVersion/blob/e07c994ddc50e00b53b728d08ab044e66c35c30f/bungee/src/main/java/us/myles/ViaVersion/bungee/platform/BungeeViaInjector.java
        Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
        getDeclaredFields0.setAccessible(true);

        Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
        for (Field classField : fields) {
            if ("modifiers".equals(classField.getName())) {
                classField.setAccessible(true);
                classField.set(field, modifiers & ~Modifier.FINAL);

                break;
            }
        }
    }

    public static Method setAccessible(Method method) {
        method.setAccessible(true);
        return method;
    }

    public static Constructor setAccessible(Constructor constructor) {
        constructor.setAccessible(true);
        return constructor;
    }

    private static Object initModifiersVarHandle() {
        try {
            VarHandle.class.getName(); // Makes this method fail-fast on JDK 8
            return MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup())
                    .findVarHandle(Field.class, "modifiers", int.class);
        } catch (IllegalAccessException | NoClassDefFoundError | NoSuchFieldException ignored) {
        }

        return null;
    }

    private static Field initModifiersField() {
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            return modifiersField;
        } catch (NoSuchFieldException ignored) {
        }

        return null;
    }

    static {
        modifiersVarHandle = initModifiersVarHandle();
        modifiersField = initModifiersField();
    }
}