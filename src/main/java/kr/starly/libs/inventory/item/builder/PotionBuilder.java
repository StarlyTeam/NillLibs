package kr.starly.libs.inventory.item.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PotionBuilder extends AbstractItemBuilder<PotionBuilder> {

    private List<PotionEffect> effects = new ArrayList<>();
    private Color color;
    private PotionType basePotionType;

    public PotionBuilder(@NotNull PotionBuilder.Type type) {
        super(type.getMaterial());
    }

    public PotionBuilder(@NotNull ItemStack base) {
        super(base);
    }

    public @NotNull PotionBuilder setColor(@NotNull Color color) {
        this.color = color;
        return this;
    }

    public @NotNull PotionBuilder setColor(@NotNull java.awt.Color color) {
        this.color = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
        return this;
    }

    public @NotNull PotionBuilder setBasePotionType(@NotNull PotionType basePotionType) {
        this.basePotionType = basePotionType;
        return this;
    }

    public @NotNull PotionBuilder addEffect(@NotNull PotionEffect effect) {
        effects.add(effect);
        return this;
    }

    @Override
    public @NotNull ItemStack get() {
        ItemStack item = super.get();
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        meta.clearCustomEffects();
        if (color != null) meta.setColor(color);
        if (basePotionType != null) meta.setBasePotionType(basePotionType);
        effects.forEach(effect -> meta.addCustomEffect(effect, true));

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public @NotNull PotionBuilder clone() {
        PotionBuilder builder = super.clone();
        builder.effects = new ArrayList<>(effects);
        return builder;
    }

    @AllArgsConstructor
    @Getter
    public enum Type {

        NORMAL(Material.POTION),
        SPLASH(Material.SPLASH_POTION),
        LINGERING(Material.LINGERING_POTION);

        private final @NotNull Material material;
    }
}