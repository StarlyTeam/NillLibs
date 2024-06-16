package kr.starly.libs.inventory.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface ItemProvider extends Supplier<@NotNull ItemStack>, Cloneable {

    @NotNull ItemProvider EMPTY = new ItemWrapper(new ItemStack(Material.AIR));

    @NotNull ItemStack get();
}