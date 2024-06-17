package kr.starly.libs.nms.v1_18_R1;

import kr.starly.libs.nms.abstraction.inventory.AnvilInventory;
import kr.starly.libs.nms.component.ComponentWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class AnvilInventoryImpl extends AnvilMenu implements AnvilInventory {

    private final Component title;
    private final List<Consumer<String>> renameHandlers;
    private final CraftInventoryView view;
    private final ServerPlayer player;

    private String text;
    private boolean open;

    public AnvilInventoryImpl(org.bukkit.entity.Player player, @NotNull ComponentWrapper title, List<Consumer<String>> renameHandlers) {
        this(((CraftPlayer) player).getHandle(), InventoryUtilsImpl.createNMSComponent(title), renameHandlers);
    }

    public AnvilInventoryImpl(ServerPlayer player, Component title, List<Consumer<String>> renameHandlers) {
        super(player.nextContainerCounter(), player.getInventory(),
                ContainerLevelAccess.create(player.level, new BlockPos(0, 0, 0)));

        this.title = title;
        this.renameHandlers = renameHandlers;
        this.player = player;

        CraftInventoryAnvil inventory = new CraftInventoryAnvil(access.getLocation(),
                inputSlots, resultSlots, this);
        this.view = new CraftInventoryView(player.getBukkitEntity(), inventory, this);
    }

    public void open() {
        open = true;
        CraftEventFactory.callInventoryOpenEvent(player, this);

        player.containerMenu = this;
        player.connection.send(new ClientboundOpenScreenPacket(containerId, MenuType.ANVIL, title));

        NonNullList<ItemStack> itemsList = NonNullList.of(ItemStack.EMPTY, getItem(0), getItem(1), getItem(2));
        player.connection.send(new ClientboundContainerSetContentPacket(getActiveWindowId(player), incrementStateId(), itemsList, ItemStack.EMPTY));

        player.initMenu(this);
    }

    public void sendItem(int slot) {
        player.connection.send(new ClientboundContainerSetSlotPacket(getActiveWindowId(player), incrementStateId(), slot, getItem(slot)));
    }

    public void setItem(int slot, ItemStack item) {
        if (slot < 2) inputSlots.setItem(slot, item);
        else resultSlots.setItem(0, item);

        if (open) sendItem(slot);
    }

    private ItemStack getItem(int slot) {
        if (slot < 2) return inputSlots.getItem(slot);
        else return resultSlots.getItem(0);
    }

    private int getActiveWindowId(ServerPlayer player) {
        return InventoryUtilsImpl.getActiveWindowId(player);
    }

    @Override
    public void setItem(int slot, org.bukkit.inventory.ItemStack itemStack) {
        setItem(slot, CraftItemStack.asNMSCopy(itemStack));
    }

    @Override
    public @NotNull Inventory getBukkitInventory() {
        return view.getTopInventory();
    }

    @Override
    public String getRenameText() {
        return text;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    // --- AnvilMenu ---

    @Override
    public CraftInventoryView getBukkitView() {
        return view;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void setItemName(String s) {
        text = s;

        if (renameHandlers != null)
            renameHandlers.forEach(handler -> handler.accept(s));

        sendItem(2);
    }

    @Override
    public void removed(Player player) {
        open = false;
    }

    @Override
    protected void clearContainer(Player player, Container container) {
        open = false;
    }

    @Override
    public void createResult() {}
}