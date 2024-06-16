package kr.starly.libs.util;

import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ItemUtils {

    public static boolean isEmpty(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getAmount() <= 0)
            return true;

        Material type = itemStack.getType();
        return type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR;
    }

    public static @Nullable ItemStack takeUnlessEmpty(@Nullable ItemStack itemStack) {
        if (isEmpty(itemStack))
            return null;

        return itemStack;
    }

    public static @Nullable ItemStack @NotNull [] clone(@Nullable ItemStack @NotNull [] array) {
        ItemStack[] clone = new ItemStack[array.length];
        for (int i = 0; i < array.length; i++) {
            ItemStack element = array[i];
            if (element != null)
                clone[i] = element.clone();
        }

        return clone;
    }
}