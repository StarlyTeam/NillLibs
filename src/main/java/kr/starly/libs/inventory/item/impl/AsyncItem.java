package kr.starly.libs.inventory.item.impl;

import kr.starly.libs.inventory.item.ItemProvider;
import kr.starly.libs.inventory.item.ItemWrapper;
import kr.starly.libs.scheduler.Do;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class AsyncItem extends AbstractItem {

    private volatile ItemProvider itemProvider;

    public AsyncItem(@Nullable ItemProvider itemProvider, @NotNull Supplier<? extends ItemProvider> providerSupplier) {
        this.itemProvider = itemProvider == null ? new ItemWrapper(new ItemStack(Material.AIR)) : itemProvider;

        Do.async(() -> {
            this.itemProvider = providerSupplier.get();
            Do.sync(this::notifyWindows);
        });
    }

    public AsyncItem(@NotNull Supplier<? extends ItemProvider> providerSupplier) {
        this(null, providerSupplier);
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemProvider;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
    }
}