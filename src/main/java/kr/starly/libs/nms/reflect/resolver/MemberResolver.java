package kr.starly.libs.nms.reflect.resolver;

import kr.starly.libs.nms.reflect.wrapper.WrapperAbstract;

import java.lang.reflect.Member;

public abstract class MemberResolver<T extends Member> extends ResolverAbstract<T> {

    protected Class<?> clazz;

    public MemberResolver(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("class cannot be null");
        }
        this.clazz = clazz;
    }

    public abstract T resolveIndex(int index) throws IndexOutOfBoundsException, ReflectiveOperationException;

    public abstract T resolveIndexSilent(int index);

    public abstract WrapperAbstract resolveIndexWrapper(int index);
}