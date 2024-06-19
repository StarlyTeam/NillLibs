package kr.starly.libs.nms.v1_21_R1;

import com.mojang.serialization.Dynamic;
import kr.starly.libs.nms.abstraction.util.ItemUtils;
import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.nms.reflect.accessor.FieldAccessor;
import kr.starly.libs.nms.reflect.resolver.FieldResolver;
import kr.starly.libs.nms.reflect.resolver.minecraft.OBCClassResolver;
import net.minecraft.nbt.*;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_21_R1.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

class ItemUtilsImpl implements ItemUtils {

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
            CompoundTag nbt = (CompoundTag) nmsStack.save(CraftRegistry.getMinecraftRegistry(), new CompoundTag());

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
                nbt = NbtIo.readCompressed(inputStream, NbtAccounter.unlimitedHeap());
            } else {
                DataInputStream dataIn = new DataInputStream(inputStream);
                nbt = NbtIo.read(dataIn);
            }

            Dynamic<Tag> converted = DataFixers.getDataFixer()
                    .update(
                            References.ITEM_STACK,
                            new Dynamic<>(NbtOps.INSTANCE, nbt),
                            3700, CraftMagicNumbers.INSTANCE.getDataVersion()
                    );

            ItemStack itemStack = ItemStack.parse(
                    CraftRegistry.getMinecraftRegistry(),
                    converted.getValue()
            ).orElse(ItemStack.EMPTY);
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