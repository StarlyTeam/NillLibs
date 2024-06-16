package kr.starly.libs.inventory.item.impl.controlitem;

import kr.starly.libs.inventory.gui.Gui;
import kr.starly.libs.inventory.item.ItemProvider;
import kr.starly.libs.inventory.item.impl.AbstractItem;
import lombok.Getter;

@Getter
public abstract class ControlItem<G extends Gui> extends AbstractItem {

    private G gui;

    public abstract ItemProvider getItemProvider(G gui);

    @Override
    public final ItemProvider getItemProvider() {
        return getItemProvider(gui);
    }

    public void setGui(G gui) {
        if (this.gui == null) {
            this.gui = gui;
        }
    }
}