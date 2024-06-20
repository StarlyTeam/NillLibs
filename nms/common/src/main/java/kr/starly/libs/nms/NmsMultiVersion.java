package kr.starly.libs.nms;

import kr.starly.libs.nms.abstraction.util.*;
import kr.starly.libs.nms.reflect.resolver.ClassResolver;
import kr.starly.libs.nms.reflect.resolver.ConstructorResolver;
import kr.starly.libs.nms.version.NmsRevision;

public class NmsMultiVersion {

    private static final NmsRevision nmsRevision = NmsRevision.REQUIRED_REVISION;

    private static final Class<InjectUtils> INJECT_UTILS_CLASS = getImplClass("InjectUtils");
    private static final Class<ItemTranslator> ITEM_TRANSLATOR_CLASS = getImplClass("ItemTranslator");

    private static final InjectUtils INJECT_UTILS = getImplInstance(INJECT_UTILS_CLASS);
    private static final ItemTranslator ITEM_TRANSLATOR = getImplInstance(ITEM_TRANSLATOR_CLASS);


    public static InjectUtils getInjectUtils() {
        return INJECT_UTILS;
    }

    public static ItemTranslator getItemTranslator() {
        return ITEM_TRANSLATOR;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getImplClass(String className) {
        return (Class<T>) new ClassResolver().resolveSilent("kr.starly.libs.nms." + nmsRevision.getPackageName() + "." + className + "Impl");
    }

    @SuppressWarnings("unchecked")
    private static <T> T getImplInstance(Class<T> clazz) {
        return (T) new ConstructorResolver(clazz).resolveIndexWrapper(0).newInstance();
    }
}