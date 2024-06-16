package kr.starly.libs.inventory.item.builder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder extends AbstractItemBuilder<ItemBuilder> {

    public ItemBuilder(Material material) {
        super(material);
    }

    public ItemBuilder(Material material, int amount) {
        super(material, amount);
    }

    public ItemBuilder(ItemStack base) {
        super(base);
    }
}