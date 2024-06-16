package kr.starly.libs.inventory.item.impl;

import kr.starly.libs.inventory.item.Item;
import kr.starly.libs.inventory.window.AbstractWindow;
import kr.starly.libs.inventory.window.Window;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractItem implements Item {

    private final Set<AbstractWindow> windows = new HashSet<>();

    @Override
    public void addWindow(AbstractWindow window) {
        windows.add(window);
    }

    @Override
    public void removeWindow(AbstractWindow window) {
        windows.remove(window);
    }

    @Override
    public Set<Window> getWindows() {
        return Collections.unmodifiableSet(windows);
    }

    @Override
    public void notifyWindows() {
        windows.forEach(w -> w.handleItemProviderUpdate(this));
    }
}