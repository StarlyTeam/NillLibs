package kr.starly.libs.inventory.item.builder;

import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;

public final class FireworkBuilder extends AbstractItemBuilder<FireworkBuilder> {

    private int power = -1;
    private List<FireworkEffect> effects = new ArrayList<>();

    public FireworkBuilder() {
        super(Material.FIREWORK_ROCKET);
    }

    public FireworkBuilder(int amount) {
        super(Material.FIREWORK_ROCKET, amount);
    }

    public FireworkBuilder(@NotNull ItemStack base) {
        super(base);
    }

    public @NotNull FireworkBuilder setPower(@Range(from = 0, to = 127) int power) {
        this.power = power;
        return this;
    }

    public @NotNull FireworkBuilder addFireworkEffect(@NotNull FireworkEffect effect) {
        effects.add(effect);
        return this;
    }

    public @NotNull FireworkBuilder addFireworkEffect(@NotNull FireworkEffect.Builder builder) {
        effects.add(builder.build());
        return this;
    }

    public @NotNull FireworkBuilder setFireworkEffects(@NotNull List<@NotNull FireworkEffect> effects) {
        this.effects = effects;
        return this;
    }

    public @NotNull FireworkBuilder clearFireworkEffects() {
        effects.clear();
        return this;
    }

    @Override
    public @NotNull ItemStack get() {
        ItemStack item = super.get();
        FireworkMeta meta = (FireworkMeta) item.getItemMeta();

        if (power != -1) meta.setPower(power);
        meta.clearEffects();
        meta.addEffects(effects);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public @NotNull FireworkBuilder clone() {
        FireworkBuilder builder = super.clone();
        builder.effects = new ArrayList<>(effects);
        return builder;
    }
}