package kr.starly.libs.nms.reflect.resolver;

import kr.starly.libs.nms.reflect.wrapper.ClassWrapper;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ClassResolver extends ResolverAbstract<Class> {

    public ClassWrapper resolveWrapper(String... names) {
        return new ClassWrapper<>(resolveSilent(names));
    }

    public Class resolveSilent(String... names) {
        try {
            return resolve(names);
        } catch (Exception ignored) {
        }

        return null;
    }

    public Class resolve(String... names) throws ClassNotFoundException {
        ResolverQuery.Builder builder = ResolverQuery.builder();
        for (String name : names) {
            builder.with(name);
        }

        try {
            return super.resolve(builder.build());
        } catch (ReflectiveOperationException ex) {
            throw (ClassNotFoundException) ex;
        }
    }

    @Override
    protected Class resolveObject(ResolverQuery query) throws ReflectiveOperationException {
        return Class.forName(query.getName());
    }

    @Override
    protected ClassNotFoundException notFoundException(String joinedNames) {
        return new ClassNotFoundException("Could not resolve class for " + joinedNames);
    }
}