package kr.starly.libs.nms.component;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class BungeeComponentWrapper implements ComponentWrapper {

    public static final BungeeComponentWrapper EMPTY = new BungeeComponentWrapper(new BaseComponent[]{new TextComponent("")});

    private final BaseComponent[] components;

    @Override
    public @NotNull String serializeToJson() {
        return ComponentSerializer.toString(components);
    }

    @Override
    public @NotNull BungeeComponentWrapper withoutPreFormatting() {
        return new BungeeComponentWrapper(BungeeComponentUtil.withoutPreFormatting(components));
    }

    @Override
    public @NotNull BungeeComponentWrapper clone() {
        try {
            var clone = (BungeeComponentWrapper) super.clone();
            for (int i = 0; i < clone.components.length; i++) {
                clone.components[i] = clone.components[i].duplicate();
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }

}
