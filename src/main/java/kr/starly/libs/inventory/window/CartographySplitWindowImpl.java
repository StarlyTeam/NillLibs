package kr.starly.libs.inventory.window;

import kr.starly.libs.inventory.gui.AbstractGui;
import kr.starly.libs.inventory.gui.Gui;
import kr.starly.libs.inventory.item.impl.SimpleItem;
import kr.starly.libs.nms.NmsMultiVersion;
import kr.starly.libs.nms.abstraction.inventory.CartographyInventory;
import kr.starly.libs.nms.component.BungeeComponentWrapper;
import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.nms.map.MapIcon;
import kr.starly.libs.nms.map.MapPatch;
import kr.starly.libs.util.MathUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class CartographySplitWindowImpl extends AbstractSplitWindow implements CartographyWindow {

    private final CartographyInventory cartographyInventory;
    private int mapId;

    public CartographySplitWindowImpl(
            @NotNull Player player,
            @Nullable ComponentWrapper title,
            @NotNull AbstractGui upperGui,
            @NotNull AbstractGui lowerGui,
            boolean closeable
    ) {
        super(player, title, createWrappingGui(upperGui), lowerGui, null, closeable);

        cartographyInventory = NmsMultiVersion.createCartographyInventory(player, title == null ? BungeeComponentWrapper.EMPTY : title);
        upperInventory = cartographyInventory.getBukkitInventory();

        resetMap();
    }

    private static AbstractGui createWrappingGui(Gui upperGui) {
        if (upperGui.getWidth() != 2 || upperGui.getHeight() != 1)
            throw new IllegalArgumentException("Gui has to be 2x1");

        Gui wrapperGui = Gui.empty(3, 1);
        wrapperGui.fillRectangle(1, 0, upperGui, true);
        return (AbstractGui) wrapperGui;
    }

    @Override
    public void updateMap(@Nullable MapPatch patch, @Nullable List<MapIcon> icons) {
        NmsMultiVersion.getPlayerUtils().sendMapUpdate(getViewer(), mapId, (byte) 0, false, patch, icons);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void resetMap() {
        mapId = -MathUtils.RANDOM.nextInt(Integer.MAX_VALUE);
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        mapMeta.setMapId(mapId);
        map.setItemMeta(mapMeta);
        getGuis()[0].setItem(0, new SimpleItem(map));
    }

    @Override
    protected void openInventory(@NotNull Player viewer) {
        cartographyInventory.open();
    }

    public static final class BuilderImpl
            extends AbstractSplitWindow.AbstractBuilder<CartographyWindow, CartographyWindow.Builder.Split>
            implements CartographyWindow.Builder.Split {

        @Override
        public @NotNull CartographyWindow build(Player viewer) {
            if (viewer == null)
                throw new IllegalStateException("Viewer is not defined.");
            if (upperGuiSupplier == null)
                throw new IllegalStateException("Upper Gui is not defined.");

            var window = new CartographySplitWindowImpl(
                    viewer,
                    title,
                    (AbstractGui) upperGuiSupplier.get(),
                    (AbstractGui) lowerGuiSupplier.get(),
                    closeable
            );

            applyModifiers(window);
            return window;
        }
    }
}