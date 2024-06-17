package kr.starly.libs.inventory.item.builder;

import kr.starly.libs.nms.NmsMultiVersion;
import kr.starly.libs.nms.component.BungeeComponentWrapper;
import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.inventory.item.ItemProvider;
import kr.starly.libs.nms.reflect.resolver.ClassResolver;
import kr.starly.libs.nms.reflect.resolver.FieldResolver;
import kr.starly.libs.nms.version.VersionUtils;
import kr.starly.libs.util.Pair;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Getter
public abstract class AbstractItemBuilder<S> implements ItemProvider {

    protected ItemStack base;
    protected Material material;
    protected @Getter int amount = 1;
    protected @Getter int damage;
    protected @Getter int customModelData;
    protected Boolean unbreakable;
    protected ComponentWrapper displayName;
    protected List<ComponentWrapper> lore;
    protected List<ItemFlag> itemFlags;
    protected Boolean hideTooltip;
    protected Boolean fireResistant;
    protected Integer maxStackSize;
    protected String rarity;
    protected Map<Enchantment, Pair<Integer, Boolean>> enchantments;
    protected List<Function<ItemStack, ItemStack>> modifiers;

    public AbstractItemBuilder(@NotNull Material material) {
        this.material = material;
    }

    public AbstractItemBuilder(@NotNull Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public AbstractItemBuilder(ItemStack base) {
        this.base = base.clone();
        this.amount = base.getAmount();
    }

    @Override
    public @NotNull ItemStack get() {
        ItemStack itemStack;
        if (base != null) {
            itemStack = base;
            itemStack.setAmount(amount);
        } else {
            itemStack = new ItemStack(material, amount);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (displayName != null)
                NmsMultiVersion.getItemUtils().setDisplayName(itemMeta, displayName);
            if (lore != null)
                NmsMultiVersion.getItemUtils().setLore(itemMeta, lore);
            if (itemMeta instanceof Damageable)
                ((Damageable) itemMeta).setDamage(damage);
            if (customModelData != 0)
                itemMeta.setCustomModelData(customModelData);
            if (unbreakable != null)
                itemMeta.setUnbreakable(unbreakable);

            if (hideTooltip != null && VersionUtils.isServerHigherOrEqual("1.20.5"))
                itemMeta.setHideTooltip(hideTooltip);
            if (fireResistant != null && VersionUtils.isServerHigherOrEqual("1.20.5"))
                itemMeta.setFireResistant(fireResistant);
            if (maxStackSize != null && VersionUtils.isServerHigherOrEqual("1.20.5"))
                itemMeta.setMaxStackSize(maxStackSize);
            if (rarity != null && VersionUtils.isServerHigherOrEqual("1.20.5"))
                itemMeta.setRarity(new FieldResolver(new ClassResolver().resolveSilent("org.bukkit.inventory.ItemRarity")).resolveAccessor(rarity).get(null));

            if (enchantments != null) {
                if (base != null)
                    itemMeta.getEnchants().forEach((enchantment, level) -> itemMeta.removeEnchant(enchantment));

                enchantments.forEach((enchantment, pair) -> itemMeta.addEnchant(enchantment, pair.getFirst(), pair.getSecond()));
            }

            if (itemFlags != null) {
                if (base != null)
                    itemMeta.removeItemFlags(itemMeta.getItemFlags().toArray(new ItemFlag[0]));

                itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
            }

            itemStack.setItemMeta(itemMeta);
        }

        if (modifiers != null) {
            for (Function<ItemStack, ItemStack> modifier : modifiers) {
                itemStack = modifier.apply(itemStack);
            }
        }

        return itemStack;
    }

    public @Nullable ItemStack getBase() {
        return base;
    }

    public @NotNull Material getMaterial() {
        return material;
    }

    public @NotNull S setMaterial(@NotNull Material material) {
        this.material = material;
        return (S) this;
    }

    public @NotNull S setAmount(int amount) {
        this.amount = amount;
        return (S) this;
    }

    public @NotNull S setDamage(int damage) {
        this.damage = damage;
        return (S) this;
    }

    public @NotNull S setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return (S) this;
    }

    public @Nullable Boolean getUnbreakable() {
        return unbreakable;
    }

    public @NotNull S setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return (S) this;
    }

    public @Nullable ComponentWrapper getDisplayName() {
        return displayName;
    }

    public @NotNull S setDisplayName(String displayName) {
        this.displayName = new BungeeComponentWrapper(TextComponent.fromLegacyText(displayName)).withoutPreFormatting();
        return (S) this;
    }

    public @NotNull S setDisplayName(BaseComponent... displayName) {
        this.displayName = new BungeeComponentWrapper(displayName).withoutPreFormatting();
        return (S) this;
    }

    public @NotNull S setDisplayName(ComponentWrapper displayName) {
        this.displayName = displayName;
        return (S) this;
    }

    public @NotNull S setLore(@NotNull List<ComponentWrapper> lore) {
        this.lore = lore.stream()
                .map(ComponentWrapper::withoutPreFormatting)
                .toList();
        return (S) this;
    }

    public @NotNull S setLegacyLore(@NotNull List<@NotNull String> lore) {
        this.lore = lore.stream()
                .map(line -> new BungeeComponentWrapper(TextComponent.fromLegacyText(line)).withoutPreFormatting())
                .collect(Collectors.toList());
        return (S) this;
    }

    public @NotNull S addLoreLines(@NotNull String... lines) {
        if (lore == null) lore = new ArrayList<>();
        for (String line : lines)
            lore.add(new BungeeComponentWrapper(TextComponent.fromLegacyText(line)).withoutPreFormatting());
        return (S) this;
    }

    public @NotNull S addLoreLines(@NotNull BaseComponent[]... lines) {
        if (lore == null) lore = new ArrayList<>();
        for (BaseComponent[] line : lines)
            lore.add(new BungeeComponentWrapper(line).withoutPreFormatting());
        return (S) this;
    }

    public @NotNull S addLoreLines(@NotNull ComponentWrapper... lines) {
        if (lore == null) lore = new ArrayList<>();
        for (ComponentWrapper line : lines)
            lore.add(line.withoutPreFormatting());
        return (S) this;
    }

    public @NotNull S addLoreLines(@NotNull List<@NotNull ComponentWrapper> list) {
        if (lore == null) lore = new ArrayList<>();
        for (ComponentWrapper line : list)
            lore.add(line.withoutPreFormatting());
        return (S) this;
    }

    public @NotNull S addLegacyLoreLines(@NotNull List<@NotNull String> lines) {
        if (lore == null) lore = new ArrayList<>();
        for (String line : lines)
            lore.add(new BungeeComponentWrapper(TextComponent.fromLegacyText(line)).withoutPreFormatting());
        return (S) this;
    }

    public @NotNull S removeLoreLine(int index) {
        if (lore != null) lore.remove(index);
        return (S) this;
    }

    public @NotNull S clearLore() {
        if (lore != null) lore.clear();
        return (S) this;
    }

    public @Nullable List<ItemFlag> getItemFlags() {
        return itemFlags;
    }

    public @NotNull S setItemFlags(@NotNull List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return (S) this;
    }

    public @NotNull S addItemFlags(@NotNull ItemFlag... itemFlags) {
        if (this.itemFlags == null) this.itemFlags = new ArrayList<>();
        this.itemFlags.addAll(Arrays.asList(itemFlags));
        return (S) this;
    }

    public @NotNull S addAllItemFlags() {
        this.itemFlags = new ArrayList<>(Arrays.asList(ItemFlag.values()));
        return (S) this;
    }

    public @NotNull S removeItemFlags(@NotNull ItemFlag... itemFlags) {
        if (this.itemFlags != null)
            this.itemFlags.removeAll(Arrays.asList(itemFlags));
        return (S) this;
    }

    public @NotNull S clearItemFlags() {
        if (itemFlags != null) itemFlags.clear();
        return (S) this;
    }

    public @Nullable Boolean isHideTooltip() {
        return hideTooltip;
    }

    public @NotNull S setHideTooltip(boolean hideTooltip) {
        this.hideTooltip = hideTooltip;
        return (S) this;
    }

    public @Nullable Boolean isFireResistant() {
        return fireResistant;
    }

    public @NotNull S setFireResistant(boolean fireResistant) {
        this.fireResistant = fireResistant;
        return (S) this;
    }

    public @Nullable Integer getMaxStackSize() {
        return maxStackSize;
    }

    public @NotNull S setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
        return (S) this;
    }

    public @Nullable String getRarity() {
        return rarity;
    }

    public @NotNull S setRarity(String rarity) {
        this.rarity = rarity;
        return (S) this;
    }

    public @Nullable Map<Enchantment, Pair<Integer, Boolean>> getEnchantments() {
        return enchantments;
    }

    public @NotNull S addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        if (enchantments == null) enchantments = new HashMap<>();
        enchantments.put(enchantment, new Pair<>(level, ignoreLevelRestriction));
        return (S) this;
    }

    public @NotNull S removeEnchantment(Enchantment enchantment) {
        if (enchantments != null) enchantments.remove(enchantment);
        return (S) this;
    }

    public @NotNull S clearEnchantments() {
        if (enchantments != null) enchantments.clear();
        return (S) this;
    }

    public @Nullable List<Function<ItemStack, ItemStack>> getModifiers() {
        return modifiers;
    }

    public @NotNull S addModifier(Function<ItemStack, ItemStack> modifier) {
        if (modifiers == null) modifiers = new ArrayList<>();
        modifiers.add(modifier);
        return (S) this;
    }

    public @NotNull S clearModifiers() {
        if (modifiers != null) modifiers.clear();
        return (S) this;
    }

    public @NotNull S clone() {
        try {
            AbstractItemBuilder<S> clone = ((AbstractItemBuilder<S>) super.clone());
            if (base != null) clone.base = base.clone();
            if (lore != null) clone.lore = new ArrayList<>(lore);
            if (itemFlags != null) clone.itemFlags = new ArrayList<>(itemFlags);
            if (enchantments != null) clone.enchantments = new HashMap<>(enchantments);
            if (modifiers != null) clone.modifiers = new ArrayList<>(modifiers);

            return (S) clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }
}