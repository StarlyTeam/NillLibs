package kr.starly.libs.nms.reflect.resolver;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ResolverAbstract<T> {

    protected final Map<ResolverQuery, T> resolvedObjects = new ConcurrentHashMap<>();

    protected T resolveSilent(ResolverQuery... queries) {
        try {
            return resolve(queries);
        } catch (Exception ignored) {
        }

        return null;
    }

    protected T resolve(ResolverQuery... queries) throws ReflectiveOperationException {
        if (queries == null || queries.length == 0) {
            throw new IllegalArgumentException("Given possibilities are empty");
        }

        for (ResolverQuery query : queries) {
            if (resolvedObjects.containsKey(query)) {
                return resolvedObjects.get(query);
            }

            try {
                T resolved = resolveObject(query);
                resolvedObjects.put(query, resolved);
                return resolved;
            } catch (ReflectiveOperationException ignored) {
            }
        }

        throw notFoundException(Arrays.asList(queries).toString());
    }

    protected abstract T resolveObject(ResolverQuery query) throws ReflectiveOperationException;

    protected ReflectiveOperationException notFoundException(String joinedNames) {
        return new ReflectiveOperationException("Objects could not be resolved: " + joinedNames);
    }
}