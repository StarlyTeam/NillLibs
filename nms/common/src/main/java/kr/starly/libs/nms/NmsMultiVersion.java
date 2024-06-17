package kr.starly.libs.nms;

import kr.starly.libs.nms.abstraction.inventory.AnvilInventory;
import kr.starly.libs.nms.abstraction.inventory.CartographyInventory;
import kr.starly.libs.nms.abstraction.util.InjectUtils;
import kr.starly.libs.nms.abstraction.util.InventoryUtils;
import kr.starly.libs.nms.abstraction.util.ItemUtils;
import kr.starly.libs.nms.abstraction.util.PlayerUtils;
import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.nms.reflect.resolver.ClassResolver;
import kr.starly.libs.nms.reflect.resolver.ConstructorResolver;
import kr.starly.libs.nms.reflect.wrapper.ConstructorWrapper;
import kr.starly.libs.nms.version.NmsRevision;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class NmsMultiVersion {

    private static final NmsRevision nmsRevision = NmsRevision.REQUIRED_REVISION;

    private static final Class<InventoryUtils> INVENTORY_UTILS_CLASS = getImplClass("InventoryUtilsImpl");
    private static final Class<ItemUtils> ITEM_UTILS_CLASS = getImplClass("ItemUtilsImpl");
    private static final Class<PlayerUtils> PLAYER_UTILS_CLASS = getImplClass("PlayerUtilsImpl");
    private static final Class<InjectUtils> INJECT_UTILS_CLASS = getImplClass("InjectUtilsImpl");
    private static final Class<AnvilInventory> ANVIL_INVENTORY_CLASS = getImplClass("AnvilInventoryImpl");
    private static final Class<CartographyInventory> CARTOGRAPHY_INVENTORY_CLASS = getImplClass("CartographyInventoryImpl");

    @SuppressWarnings("unchecked")
    private static final ConstructorWrapper<AnvilInventory> ANVIL_INVENTORY_CONSTRUCTOR =
            new ConstructorResolver(ANVIL_INVENTORY_CLASS).resolveWrapper(new Class<?>[]{Player.class, ComponentWrapper.class, List.class});
    @SuppressWarnings("unchecked")
    private static final ConstructorWrapper<CartographyInventory> CARTOGRAPHY_INVENTORY_CONSTRUCTOR =
            new ConstructorResolver(CARTOGRAPHY_INVENTORY_CLASS).resolveWrapper(new Class<?>[]{Player.class, ComponentWrapper.class});

    private static final InventoryUtils INVENTORY_UTILS = (InventoryUtils) new ConstructorResolver(INVENTORY_UTILS_CLASS).resolveIndexWrapper(0).newInstance();
    private static final ItemUtils ITEM_UTILS = (ItemUtils) new ConstructorResolver(ITEM_UTILS_CLASS).resolveIndexWrapper(0).newInstance();
    private static final PlayerUtils PLAYER_UTILS = (PlayerUtils) new ConstructorResolver(PLAYER_UTILS_CLASS).resolveIndexWrapper(0).newInstance();
    private static final InjectUtils INJECT_UTILS = (InjectUtils) new ConstructorResolver(INJECT_UTILS_CLASS).resolveIndexWrapper(0).newInstance();

    public static InventoryUtils getInventoryUtils() {
        return INVENTORY_UTILS;
    }

    public static ItemUtils getItemUtils() {
        return ITEM_UTILS;
    }

    public static PlayerUtils getPlayerUtils() {
        return PLAYER_UTILS;
    }

    public static InjectUtils getInjectUtils() {
        return INJECT_UTILS;
    }

    public static AnvilInventory createAnvilInventory(@NotNull Player player, @Nullable ComponentWrapper title, @Nullable List<@NotNull Consumer<String>> renameHandlers) {
        return ANVIL_INVENTORY_CONSTRUCTOR.newInstance(player, title, renameHandlers);
    }

    public static CartographyInventory createCartographyInventory(@NotNull Player player, @Nullable ComponentWrapper title) {
        return CARTOGRAPHY_INVENTORY_CONSTRUCTOR.newInstance(player, title);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getImplClass(String className) {
        return (Class<T>) new ClassResolver().resolveSilent("kr.starly.libs.nms." + nmsRevision.getPackageName() + "." + className);
    }
}