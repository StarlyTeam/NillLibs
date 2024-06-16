package kr.starly.libs.inventory.window;

import kr.starly.libs.nms.map.MapIcon;
import kr.starly.libs.nms.map.MapPatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public interface CartographyWindow extends Window {

    static @NotNull Builder.Single single() {
        return new CartographySingleWindowImpl.BuilderImpl();
    }

    static @NotNull CartographyWindow single(@NotNull Consumer<Builder.@NotNull Single> consumer) {
        Builder.Single builder = single();
        consumer.accept(builder);
        return builder.build();
    }

    static @NotNull Builder.Split split() {
        return new CartographySplitWindowImpl.BuilderImpl();
    }

    static @NotNull CartographyWindow split(@NotNull Consumer<Builder.@NotNull Split> consumer) {
        Builder.Split builder = split();
        consumer.accept(builder);
        return builder.build();
    }

    void updateMap(@Nullable MapPatch patch, @Nullable List<MapIcon> icons);

    default void updateMap(@Nullable MapPatch patch) {
        updateMap(patch, null);
    }

    default void updateMap(@Nullable List<MapIcon> icons) {
        updateMap(null, icons);
    }

    void resetMap();

    interface Builder<S extends Builder<S>> extends Window.Builder<CartographyWindow, S> {

        interface Single extends Builder<Single>, Window.Builder.Single<CartographyWindow, Single> {
        }

        interface Split extends Builder<Split>, Window.Builder.Double<CartographyWindow, Split> {
        }
    }
}