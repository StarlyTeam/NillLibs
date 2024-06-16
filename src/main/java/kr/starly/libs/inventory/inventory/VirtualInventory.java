package kr.starly.libs.inventory.inventory;

import kr.starly.libs.inventory.InventoryFramework;
import kr.starly.libs.nms.NmsMultiVersion;
import kr.starly.libs.nms.util.DataUtils;
import kr.starly.libs.util.ItemUtils;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.logging.Level;

public class VirtualInventory extends Inventory {

    private final @NotNull UUID uuid;
    private int size;
    private @Nullable ItemStack @NotNull [] items;
    private @Setter int @NotNull [] maxStackSizes;
    private @Nullable List<@NotNull BiConsumer<@NotNull Integer, @NotNull Integer>> resizeHandlers;

    public VirtualInventory(@Nullable UUID uuid, int size, @Nullable ItemStack @Nullable [] items, int @Nullable [] maxStackSizes) {
        this.uuid = uuid == null ? new UUID(0L, 0L) : uuid;
        this.size = size;
        this.items = items == null ? new ItemStack[size] : items;

        if (maxStackSizes == null) {
            this.maxStackSizes = new int[size];
            Arrays.fill(this.maxStackSizes, 64);
        } else {
            this.maxStackSizes = maxStackSizes;
        }

        if (size != this.items.length)
            throw new IllegalArgumentException("Inventory size does not match items array length");
        if (size != this.maxStackSizes.length)
            throw new IllegalArgumentException("Inventory size does not match maxStackSizes array length");
        if (items != null) {
            for (ItemStack item : items) {
                if (item != null && item.getType() == Material.AIR)
                    throw new IllegalArgumentException("Items array may not contain air items!");
            }
        }
    }

    public VirtualInventory(int size, @Nullable ItemStack @Nullable [] items, int @Nullable [] maxStackSizes) {
        this(null, size, items, maxStackSizes);
    }

    public VirtualInventory(@Nullable UUID uuid, @Nullable ItemStack @NotNull [] items, int @Nullable [] maxStackSizes) {
        this(uuid, items.length, items, maxStackSizes);
    }

    public VirtualInventory(@Nullable ItemStack @NotNull [] items, int @Nullable [] maxStackSizes) {
        this(null, items.length, items, maxStackSizes);
    }

    public VirtualInventory(@Nullable UUID uuid, @Nullable ItemStack @NotNull [] items) {
        this(uuid, items.length, items, null);
    }

    public VirtualInventory(@Nullable ItemStack @NotNull [] items) {
        this(null, items.length, items, null);
    }

    public VirtualInventory(@Nullable UUID uuid, int @NotNull [] maxStackSizes) {
        this(uuid, maxStackSizes.length, null, maxStackSizes);
    }

    public VirtualInventory(int @NotNull [] maxStackSizes) {
        this(null, maxStackSizes.length, null, maxStackSizes);
    }

    public VirtualInventory(@Nullable UUID uuid, int size) {
        this(uuid, size, null, null);
    }

    public VirtualInventory(int size) {
        this(null, size, null, null);
    }

    public VirtualInventory(VirtualInventory inventory) {
        this(inventory.uuid, inventory.size, ItemUtils.clone(inventory.items), inventory.maxStackSizes.clone());
        setGuiPriority(inventory.getGuiPriority());
        setPreUpdateHandler(inventory.getPreUpdateHandler());
        setPostUpdateHandler(inventory.getPostUpdateHandler());
        setResizeHandlers(inventory.getResizeHandlers());
    }

    public static VirtualInventory deserialize(byte[] bytes) {
        return deserialize(new ByteArrayInputStream(bytes));
    }

    public static VirtualInventory deserialize(InputStream in) {
        try {
            DataInputStream din = new DataInputStream(in);
            UUID uuid = new UUID(din.readLong(), din.readLong());

            byte id = din.readByte(); // id, pre v1.0: 3, v1.0: 4
            if (id == 3) {
                DataUtils.readByteArray(din);
            }

            ItemStack[] items = Arrays.stream(DataUtils.read2DByteArray(din)).map(data -> {
                        if (data.length != 0) {
                            return NmsMultiVersion.getItemUtils().deserializeItemStack(data, true);
                        } else return null;
                    }
            ).toArray(ItemStack[]::new);

            return new VirtualInventory(uuid, items);
        } catch (IOException ex) {
            InventoryFramework.getLogger().log(Level.SEVERE, "Failed to deserialize VirtualInventory", ex);
        }

        return null;
    }

    public byte[] serialize() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serialize(out);
        return out.toByteArray();
    }

    public void serialize(OutputStream out) {
        try {
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeLong(uuid.getMostSignificantBits());
            dos.writeLong(uuid.getLeastSignificantBits());
            dos.writeByte((byte) 4); // id, pre v1.0: 3, v1.0: 4

            byte[][] items = Arrays.stream(this.items).map(itemStack -> {
                        if (itemStack != null) {
                            return NmsMultiVersion.getItemUtils().serializeItemStack(itemStack, true);
                        } else return new byte[0];
                    }
            ).toArray(byte[][]::new);

            DataUtils.write2DByteArray(dos, items);

            dos.flush();
        } catch (IOException ex) {
            InventoryFramework.getLogger().log(Level.SEVERE, "Failed to serialize VirtualInventory", ex);
        }
    }

    public void setResizeHandlers(@Nullable List<@NotNull BiConsumer<@NotNull Integer, @NotNull Integer>> resizeHandlers) {
        this.resizeHandlers = resizeHandlers;
    }

    public @Nullable List<@NotNull BiConsumer<@NotNull Integer, @NotNull Integer>> getResizeHandlers() {
        return resizeHandlers;
    }

    public void addResizeHandler(@NotNull BiConsumer<@NotNull Integer, @NotNull Integer> resizeHandler) {
        if (resizeHandlers == null)
            resizeHandlers = new ArrayList<>();

        resizeHandlers.add(resizeHandler);
    }

    public void removeResizeHandler(@NotNull BiConsumer<@NotNull Integer, @NotNull Integer> resizeHandler) {
        if (resizeHandlers != null)
            resizeHandlers.remove(resizeHandler);
    }

    public void resize(int size) {
        if (this.size == size)
            return;

        int previousSize = this.size;

        this.size = size;
        this.items = Arrays.copyOf(items, size);
        this.maxStackSizes = Arrays.copyOf(maxStackSizes, size);

        if (size > previousSize) {
            int stackSize = previousSize != 0 ? maxStackSizes[previousSize - 1] : 64;
            Arrays.fill(maxStackSizes, previousSize, maxStackSizes.length, stackSize);
        }

        if (resizeHandlers != null) {
            for (BiConsumer<Integer, Integer> resizeHandler : resizeHandlers) {
                resizeHandler.accept(previousSize, size);
            }
        }
    }


    public void setMaxStackSize(int slot, int maxStackSize) {
        maxStackSizes[slot] = maxStackSize;
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int @NotNull [] getMaxStackSizes() {
        return maxStackSizes.clone();
    }

    @Override
    public int getMaxSlotStackSize(int slot) {
        return maxStackSizes[slot];
    }

    @Override
    public @Nullable ItemStack @NotNull [] getItems() {
        return ItemUtils.clone(items);
    }

    @Override
    public @Nullable ItemStack @NotNull [] getUnsafeItems() {
        return items;
    }

    @Override
    public @Nullable ItemStack getItem(int slot) {
        ItemStack itemStack = items[slot];
        return itemStack != null ? itemStack.clone() : null;
    }

    @Override
    public ItemStack getUnsafeItem(int slot) {
        return items[slot];
    }

    @Override
    protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
        items[slot] = itemStack != null ? itemStack.clone() : null;
    }

    @Override
    protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
        items[slot] = itemStack;
    }
}