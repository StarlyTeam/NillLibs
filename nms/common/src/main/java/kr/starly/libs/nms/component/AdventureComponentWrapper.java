package kr.starly.libs.nms.component;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class AdventureComponentWrapper implements ComponentWrapper {

    public static final AdventureComponentWrapper EMPTY = new AdventureComponentWrapper(Component.empty());

    private final Component component;

    @Override
    public @NotNull String serializeToJson() {
        return GsonComponentSerializer.gson().serialize(component);
    }

    @Override
    public @NotNull AdventureComponentWrapper withoutPreFormatting() {
        return new AdventureComponentWrapper(AdventureComponentUtil.withoutPreFormatting(component));
    }

    @Override
    public @NotNull AdventureComponentWrapper clone() {
        try {
            return (AdventureComponentWrapper) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }
}