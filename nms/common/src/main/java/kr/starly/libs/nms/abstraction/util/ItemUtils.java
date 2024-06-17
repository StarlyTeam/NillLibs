package kr.starly.libs.nms.abstraction.util;

import kr.starly.libs.nms.component.ComponentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface ItemUtils {

    byte[] serializeItemStack(@NotNull ItemStack itemStack, boolean compressed);

    void serializeItemStack(@NotNull ItemStack itemStack, @NotNull OutputStream outputStream, boolean compressed);

    ItemStack deserializeItemStack(byte[] data, boolean compressed);

    ItemStack deserializeItemStack(@NotNull InputStream inputStream, boolean compressed);

    void setDisplayName(@NotNull ItemMeta itemMeta, @NotNull ComponentWrapper name);

    void setLore(@NotNull ItemMeta itemMeta, @NotNull List<@NotNull ComponentWrapper> lore);
}