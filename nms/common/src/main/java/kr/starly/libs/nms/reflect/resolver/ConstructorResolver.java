package kr.starly.libs.nms.reflect.resolver;

import kr.starly.libs.nms.reflect.util.AccessUtil;
import kr.starly.libs.nms.reflect.wrapper.ConstructorWrapper;

import java.lang.reflect.Constructor;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConstructorResolver extends MemberResolver<Constructor> {

    public ConstructorResolver(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public Constructor resolveIndex(int index) throws IndexOutOfBoundsException, ReflectiveOperationException {
        return AccessUtil.setAccessible(this.clazz.getDeclaredConstructors()[index]);
    }

    @Override
    public Constructor resolveIndexSilent(int index) {
        try {
            return resolveIndex(index);
        } catch (IndexOutOfBoundsException | ReflectiveOperationException ignored) {
        }

        return null;
    }

    @Override
    public ConstructorWrapper resolveIndexWrapper(int index) {
        return new ConstructorWrapper<>(resolveIndexSilent(index));
    }

    public ConstructorWrapper resolveWrapper(Class<?>[]... types) {
        return new ConstructorWrapper<>(resolveSilent(types));
    }

    public Constructor resolveSilent(Class<?>[]... types) {
        try {
            return resolve(types);
        } catch (Exception ignored) {
        }

        return null;
    }

    public Constructor resolve(Class<?>[]... types) throws NoSuchMethodException {
        ResolverQuery.Builder builder = ResolverQuery.builder();
        for (Class<?>[] type : types) {
            builder.with(type);
        }

        try {
            return super.resolve(builder.build());
        } catch (ReflectiveOperationException e) {
            throw (NoSuchMethodException) e;
        }
    }

    @Override
    protected Constructor resolveObject(ResolverQuery query) throws ReflectiveOperationException {
        return AccessUtil.setAccessible(this.clazz.getDeclaredConstructor(query.getTypes()));
    }

    public Constructor resolveFirstConstructor() throws ReflectiveOperationException {
        for (Constructor constructor : this.clazz.getDeclaredConstructors()) {
            return AccessUtil.setAccessible(constructor);
        }

        return null;
    }

    public Constructor resolveFirstConstructorSilent() {
        try {
            return resolveFirstConstructor();
        } catch (Exception ignored) {
        }

        return null;
    }

    public Constructor resolveLastConstructor() throws ReflectiveOperationException {
        Constructor constructor = null;
        for (Constructor constructor1 : this.clazz.getDeclaredConstructors()) {
            constructor = constructor1;
        }

        if (constructor != null) {
            return AccessUtil.setAccessible(constructor);
        }

        return null;
    }

    public Constructor resolveLastConstructorSilent() {
        try {
            return resolveLastConstructor();
        } catch (Exception ignored) {
        }

        return null;
    }

    @Override
    protected NoSuchMethodException notFoundException(String joinedNames) {
        return new NoSuchMethodException("Could not resolve constructor for " + joinedNames + " in class " + this.clazz);
    }
}