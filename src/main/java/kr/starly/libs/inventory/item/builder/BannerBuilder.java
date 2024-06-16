package kr.starly.libs.inventory.item.builder;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class BannerBuilder extends AbstractItemBuilder<BannerBuilder> {

    private List<Pattern> patterns = new ArrayList<>();

    public BannerBuilder(@NotNull Material material) {
        super(material);
    }

    public BannerBuilder(@NotNull Material material, int amount) {
        super(material, amount);
    }

    public BannerBuilder(@NotNull ItemStack base) {
        super(base);
    }

    public @NotNull BannerBuilder addPattern(@NotNull Pattern pattern) {
        patterns.add(pattern);
        return this;
    }

    public @NotNull BannerBuilder addPattern(@NotNull DyeColor color, @NotNull PatternType type) {
        patterns.add(new Pattern(color, type));
        return this;
    }

    public @NotNull BannerBuilder setPatterns(@NotNull List<@NotNull Pattern> patterns) {
        this.patterns = patterns;
        return this;
    }

    public @NotNull BannerBuilder clearPatterns() {
        patterns.clear();
        return this;
    }

    @Override
    public @NotNull ItemStack get() {
        ItemStack item = super.get();
        BannerMeta meta = (BannerMeta) item.getItemMeta();

        meta.setPatterns(patterns);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public @NotNull BannerBuilder clone() {
        BannerBuilder builder = super.clone();
        builder.patterns = new ArrayList<>(patterns);
        return builder;
    }
}