package kr.starly.libs.nms.reflect.accessor;

import lombok.Getter;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.PrivilegedAction;

@Getter
@SuppressWarnings({"deprecation", "removal", "unchecked"})
public class FieldAccessor {

    private final Field field;

    public FieldAccessor(Field field) {
        this.field = field;
        if (field == null) return;

        try {
            field.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    public boolean hasField() {
        return field != null;
    }

    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    public boolean isPublic() {
        return Modifier.isPublic(field.getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    public <T> T get(Object obj) {
        try {
            return (T) field.get(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T> void set(Object obj, T value) {
        setField(obj, value, field);
    }


    private static void setField(Object object, Object value, Field foundField) {
        boolean isStatic = (foundField.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
        if (isStatic) {
            setStaticFieldUsingUnsafe(foundField, value);
        } else {
            setFieldUsingUnsafe(foundField, object, value);
        }
    }

    private static void setStaticFieldUsingUnsafe(final Field field, final Object newValue) {
        try {
            field.setAccessible(true);

            int fieldModifiersMask = field.getModifiers();
            boolean isFinalModifierPresent = (fieldModifiersMask & Modifier.FINAL) == Modifier.FINAL;
            if (isFinalModifierPresent) {
                java.security.AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                    try {
                        Unsafe unsafe = getUnsafe();
                        long offset = unsafe.staticFieldOffset(field);
                        Object base = unsafe.staticFieldBase(field);

                        setFieldUsingUnsafe(base, field.getType(), offset, newValue, unsafe);
                        return null;
                    } catch (Throwable ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } else {
                field.set(null, newValue);
            }
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void setFieldUsingUnsafe(final Field field, final Object object, final Object newValue) {
        try {
            field.setAccessible(true);

            int fieldModifiersMask = field.getModifiers();
            boolean isFinalModifierPresent = (fieldModifiersMask & Modifier.FINAL) == Modifier.FINAL;
            if (isFinalModifierPresent) {
                java.security.AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                    try {
                        Unsafe unsafe = getUnsafe();
                        long offset = unsafe.objectFieldOffset(field);

                        setFieldUsingUnsafe(object, field.getType(), offset, newValue, unsafe);
                        return null;
                    } catch (Throwable ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } else {
                try {
                    field.set(object, newValue);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Unsafe getUnsafe() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);

        return (Unsafe) field.get(null);
    }

    private static void setFieldUsingUnsafe(Object base, Class type, long offset, Object newValue, Unsafe unsafe) {
        if (type == Integer.TYPE) {
            unsafe.putInt(base, offset, ((Integer) newValue));
        } else if (type == Short.TYPE) {
            unsafe.putShort(base, offset, ((Short) newValue));
        } else if (type == Long.TYPE) {
            unsafe.putLong(base, offset, ((Long) newValue));
        } else if (type == Byte.TYPE) {
            unsafe.putByte(base, offset, ((Byte) newValue));
        } else if (type == Boolean.TYPE) {
            unsafe.putBoolean(base, offset, ((Boolean) newValue));
        } else if (type == Float.TYPE) {
            unsafe.putFloat(base, offset, ((Float) newValue));
        } else if (type == Double.TYPE) {
            unsafe.putDouble(base, offset, ((Double) newValue));
        } else if (type == Character.TYPE) {
            unsafe.putChar(base, offset, ((Character) newValue));
        } else {
            unsafe.putObject(base, offset, newValue);
        }
    }
}