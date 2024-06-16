package kr.starly.libs.inventory.animation;

import kr.starly.libs.inventory.gui.Gui;
import kr.starly.libs.inventory.window.Window;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

public interface Animation {

    void setGui(Gui gui);

    void setWindows(@NotNull List<Window> windows);

    void setSlots(List<Integer> slots);

    void addShowHandler(@NotNull BiConsumer<Integer, Integer> show);

    void addFinishHandler(@NotNull Runnable finish);

    void start();

    void cancel();
}