package kr.starly.libs.inventory.item.impl;

import kr.starly.libs.inventory.item.Click;
import kr.starly.libs.inventory.item.ItemProvider;
import kr.starly.libs.inventory.item.ItemWrapper;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SimpleItem extends AbstractItem {

    protected final @Getter ItemProvider itemProvider;
    protected final Consumer<Click> clickHandler;

    public SimpleItem(@NotNull ItemProvider itemProvider) {
        this.itemProvider = itemProvider;
        this.clickHandler = null;
    }

    public SimpleItem(@NotNull ItemStack itemStack) {
        this.itemProvider = new ItemWrapper(itemStack);
        this.clickHandler = null;
    }

    public SimpleItem(@NotNull ItemProvider itemProvider, @Nullable Consumer<@NotNull Click> clickHandler) {
        this.itemProvider = itemProvider;
        this.clickHandler = clickHandler;
    }

    public SimpleItem(@NotNull ItemStack itemStack, @Nullable Consumer<@NotNull Click> clickHandler) {
        this.itemProvider = new ItemWrapper(itemStack);
        this.clickHandler = clickHandler;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickHandler != null) clickHandler.accept(new Click(event));
    }
}