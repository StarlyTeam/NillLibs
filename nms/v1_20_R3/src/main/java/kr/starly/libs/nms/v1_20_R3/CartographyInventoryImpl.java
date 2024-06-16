package kr.starly.libs.nms.v1_20_R3;

import kr.starly.libs.nms.abstraction.inventory.CartographyInventory;
import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.nms.reflect.accessor.FieldAccessor;
import kr.starly.libs.nms.reflect.resolver.FieldResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventoryCartography;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

class CartographyInventoryImpl extends CartographyTableMenu implements CartographyInventory {

    private static final FieldAccessor RESULT_CONTAINER_FIELD = new FieldResolver(CartographyTableMenu.class).resolveAccessor("resultContainer");

    private final ResultContainer resultContainer = RESULT_CONTAINER_FIELD.get(this);
    private final Component title;
    private final CraftInventoryView view;
    private final ServerPlayer player;

    private boolean open;

    public CartographyInventoryImpl(Player player, @NotNull ComponentWrapper title) {
        this(((CraftPlayer) player).getHandle(), InventoryUtilsImpl.createNMSComponent(title));
    }

    public CartographyInventoryImpl(ServerPlayer player, Component title) {
        super(player.nextContainerCounter(), player.getInventory(), ContainerLevelAccess.create(player.level(), new BlockPos(0, 0, 0)));

        this.player = player;
        this.title = title;
        CraftInventoryCartography inventory = new CraftInventoryCartography(container, resultContainer);
        view = new CraftInventoryView(player.getBukkitEntity(), inventory, this);
    }

    public void open() {
        open = true;
        CraftEventFactory.callInventoryOpenEvent(player, this);

        player.containerMenu = this;
        player.connection.send(new ClientboundOpenScreenPacket(containerId, MenuType.CARTOGRAPHY_TABLE, title));

        NonNullList<ItemStack> itemsList = NonNullList.of(ItemStack.EMPTY, getItem(0), getItem(1), getItem(2));
        player.connection.send(new ClientboundContainerSetContentPacket(InventoryUtilsImpl.getActiveWindowId(player), incrementStateId(), itemsList, ItemStack.EMPTY));

        player.initMenu(this);
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    public void sendItem(int slot) {
        player.connection.send(new ClientboundContainerSetSlotPacket(InventoryUtilsImpl.getActiveWindowId(player), slot, incrementStateId(), getItem(slot)));
    }

    public void setItem(int slot, ItemStack item) {
        if (slot < 2) container.setItem(slot, item);
        else resultContainer.setItem(0, item);

        if (open) sendItem(slot);
    }

    private ItemStack getItem(int slot) {
        if (slot < 2) return container.getItem(slot);
        else return resultContainer.getItem(0);
    }

    @Override
    public void setItem(int slot, org.bukkit.inventory.ItemStack itemStack) {
        setItem(slot, CraftItemStack.asNMSCopy(itemStack));
    }

    @Override
    public Inventory getBukkitInventory() {
        return view.getTopInventory();
    }

    // --- CartographyTableMenu ---

    @Override
    public CraftInventoryView getBukkitView() {
        return view;
    }

    @Override
    public void slotsChanged(Container container) {
    }

    @Override
    public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player entityhuman, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemstack, Slot slot) {
        return true;
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player entityhuman) {
        return true;
    }

    @Override
    protected void clearContainer(net.minecraft.world.entity.player.Player entityhuman, Container container) {}
}