package kr.starly.libs.nms.v1_20_R1;

import kr.starly.libs.nms.abstraction.util.ItemUtils;
import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.nms.reflect.accessor.FieldAccessor;
import kr.starly.libs.nms.reflect.resolver.FieldResolver;
import kr.starly.libs.nms.reflect.resolver.minecraft.OBCClassResolver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class ItemUtilsImpl implements ItemUtils {

    @Override
    public byte[] serializeItemStack(org.bukkit.inventory.@NotNull ItemStack itemStack, boolean compressed) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializeItemStack(itemStack, out, compressed);
        return out.toByteArray();
    }

    @Override
    public void serializeItemStack(org.bukkit.inventory.@NotNull ItemStack itemStack, @NotNull OutputStream outputStream, boolean compressed) {
        try {
            ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
            CompoundTag nbt = nmsStack.save(new CompoundTag());

            if (compressed) {
                NbtIo.writeCompressed(nbt, outputStream);
            } else {
                DataOutputStream dataOut = new DataOutputStream(outputStream);
                NbtIo.write(nbt, dataOut);
            }

            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public org.bukkit.inventory.ItemStack deserializeItemStack(byte[] data, boolean compressed) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        return deserializeItemStack(in, compressed);
    }

    @Override
    public org.bukkit.inventory.ItemStack deserializeItemStack(@NotNull InputStream inputStream, boolean compressed) {
        try {
            CompoundTag nbt;
            if (compressed) {
                nbt = NbtIo.readCompressed(inputStream);
            } else {
                DataInputStream dataIn = new DataInputStream(inputStream);
                nbt = NbtIo.read(dataIn);
            }

            ItemStack itemStack = ItemStack.of(nbt);
            return CraftItemStack.asCraftMirror(itemStack);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void setDisplayName(@NotNull ItemMeta itemMeta, @NotNull ComponentWrapper name) {
        FieldAccessor setLore = new FieldResolver(new OBCClassResolver().resolveSilent("inventory.CraftMetaItem")).resolveAccessor("displayName");
        setLore.set(itemMeta, InventoryUtilsImpl.createNMSComponent(name));
    }

    @Override
    public void setLore(@NotNull ItemMeta itemMeta, @NotNull List<@NotNull ComponentWrapper> lore) {
        FieldAccessor setLore = new FieldResolver(new OBCClassResolver().resolveSilent("inventory.CraftMetaItem")).resolveAccessor("lore");
        setLore.set(itemMeta, lore.stream().map(InventoryUtilsImpl::createNMSComponent).collect(Collectors.toList()));
    }
}