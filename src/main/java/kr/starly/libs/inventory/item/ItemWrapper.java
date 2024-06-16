package kr.starly.libs.inventory.item;

import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ItemWrapper implements ItemProvider {

    private ItemStack itemStack;

    @Override
    public @NotNull ItemStack get() {
        return itemStack;
    }

    @Override
    public ItemWrapper clone() {
        try {
            ItemWrapper clone = (ItemWrapper) super.clone();
            clone.itemStack = itemStack.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new Error(ex);
        }
    }
}