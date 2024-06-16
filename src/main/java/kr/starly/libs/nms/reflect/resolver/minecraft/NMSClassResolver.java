package kr.starly.libs.nms.reflect.resolver.minecraft;

import kr.starly.libs.nms.reflect.resolver.ClassResolver;

@SuppressWarnings("rawtypes")
public class NMSClassResolver extends ClassResolver {

    @Override
    public Class resolve(String... names) throws ClassNotFoundException {
        for (int i = 0; i < names.length; i++) {
            if (!names[i].startsWith("net.minecraft")) {
                names[i] = "net.minecraft." + names[i];
            }
        }

        return super.resolve(names);
    }
}