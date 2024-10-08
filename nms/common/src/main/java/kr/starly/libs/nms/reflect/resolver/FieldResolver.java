package kr.starly.libs.nms.reflect.resolver;

import kr.starly.libs.nms.reflect.accessor.FieldAccessor;
import kr.starly.libs.nms.reflect.util.AccessUtil;
import kr.starly.libs.nms.reflect.wrapper.WrapperAbstract;

import java.lang.reflect.Field;

@SuppressWarnings("rawtypes")
public class FieldResolver extends MemberResolver<Field> {

    public FieldResolver(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public Field resolveIndex(int index) throws IndexOutOfBoundsException, ReflectiveOperationException {
        return AccessUtil.setAccessible(this.clazz.getDeclaredFields()[index]);
    }

    @Override
    public Field resolveIndexSilent(int index) {
        try {
            return resolveIndex(index);
        } catch (IndexOutOfBoundsException | ReflectiveOperationException ignored) {
        }

        return null;
    }

    @Deprecated(forRemoval = true)
    @Override
    public WrapperAbstract resolveIndexWrapper(int index) {
        throw new UnsupportedOperationException("FieldWrapper is deleted.");
    }

    public FieldAccessor resolveIndexAccessor(int index) {
        return new FieldAccessor(resolveIndexSilent(index));
    }

    public FieldAccessor resolveAccessor(String... names) {
        return new FieldAccessor(resolveSilent(names));
    }

    public Field resolveSilent(String... names) {
        try {
            return resolve(names);
        } catch (Exception ignored) {
        }

        return null;
    }

    public Field resolve(String... names) throws NoSuchFieldException {
        ResolverQuery.Builder builder = ResolverQuery.builder();
        for (String name : names) {
            builder.with(name);
        }

        try {
            return super.resolve(builder.build());
        } catch (ReflectiveOperationException e) {
            throw (NoSuchFieldException) e;
        }
    }

    public Field resolveSilent(ResolverQuery... queries) {
        try {
            return resolve(queries);
        } catch (Exception ignored) {
        }

        return null;
    }

    public Field resolve(ResolverQuery... queries) throws NoSuchFieldException {
        try {
            return super.resolve(queries);
        } catch (ReflectiveOperationException ex) {
            throw (NoSuchFieldException) ex;
        }
    }

    @Override
    protected Field resolveObject(ResolverQuery query) throws ReflectiveOperationException {
        return resolveObject(query, this.clazz);
    }

    private Field resolveObject(ResolverQuery query, Class<?> clazz) {
        Field field = null;
        if (query.getTypes() == null || query.getTypes().length == 0) {
            try {
                field = AccessUtil.setAccessible(clazz.getDeclaredField(query.getName()));
            } catch (ReflectiveOperationException ignored) {
            }
        } else {
            for (Field f : clazz.getDeclaredFields()) {
                if (f.getName().equals(query.getName())) {
                    for (Class<?> type : query.getTypes()) {
                        if (f.getType().equals(type)) {
                            field = AccessUtil.setAccessible(f);
                        }
                    }
                }
            }
        }

        if (field == null) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return resolveObject(query, superclass);
            }
        }

        return field;
    }

    public Field resolveByFirstType(Class<?> type) throws ReflectiveOperationException {
        for (Field field : this.clazz.getDeclaredFields()) {
            if (field.getType().equals(type)) {
                return AccessUtil.setAccessible(field);
            }
        }

        throw new NoSuchFieldException("Could not resolve field of type '" + type.toString() + "' in class " + this.clazz);
    }

    public FieldAccessor resolveByFirstTypeAccessor(Class<?> type) {
        return new FieldAccessor(resolveByFirstTypeSilent(type));
    }

    public Field resolveByFirstTypeSilent(Class<?> type) {
        try {
            return resolveByFirstType(type);
        } catch (Exception ignored) {
        }

        return null;
    }

    public Field resolveByFirstExtendingType(Class<?> type) throws ReflectiveOperationException {
        for (Field field : this.clazz.getDeclaredFields()) {
            if (type.isAssignableFrom(field.getType())) {
                return AccessUtil.setAccessible(field);
            }
        }

        throw new NoSuchFieldException("Could not resolve field of type '" + type.toString() + "' in class " + this.clazz);
    }

    public Field resolveByFirstExtendingTypeSilent(Class<?> type) {
        try {
            return resolveByFirstExtendingType(type);
        } catch (Exception ignored) {
        }

        return null;
    }

    public FieldAccessor resolveByFirstExtendingTypeAccessor(Class<?> type) {
        return new FieldAccessor(resolveByFirstExtendingTypeSilent(type));
    }

    public Field resolveByLastType(Class<?> type) throws ReflectiveOperationException {
        Field field = null;
        for (Field field1 : this.clazz.getDeclaredFields()) {
            if (field1.getType().equals(type)) {
                field = field1;
            }
        }

        if (field == null) {
            throw new NoSuchFieldException("Could not resolve field of type '" + type.toString() + "' in class " + this.clazz);
        }

        return AccessUtil.setAccessible(field);
    }

    public Field resolveByLastTypeSilent(Class<?> type) {
        try {
            return resolveByLastType(type);
        } catch (Exception ignored) {
        }

        return null;
    }

    public FieldAccessor resolveByLastTypeAccessor(Class<?> type) {
        return new FieldAccessor(resolveByLastTypeSilent(type));
    }

    public Field resolveByLastExtendingType(Class<?> type) throws ReflectiveOperationException {
        Field field = null;
        for (Field field1 : this.clazz.getDeclaredFields()) {
            if (type.isAssignableFrom(field1.getType())) {
                field = field1;
            }
        }

        if (field == null) {
            throw new NoSuchFieldException("Could not resolve field of type '" + type.toString() + "' in class " + this.clazz);
        }

        return AccessUtil.setAccessible(field);
    }

    public Field resolveByLastExtendingTypeSilent(Class<?> type) {
        try {
            return resolveByLastExtendingType(type);
        } catch (Exception ignored) {
        }

        return null;
    }

    public FieldAccessor resolveByLastExtendingTypeAccessor(Class<?> type) {
        return new FieldAccessor(resolveByLastExtendingTypeSilent(type));
    }

    @Override
    protected NoSuchFieldException notFoundException(String joinedNames) {
        return new NoSuchFieldException("Could not resolve field for " + joinedNames + " in class " + this.clazz);
    }
}