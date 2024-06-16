package kr.starly.libs.util;

import lombok.NoArgsConstructor;
import org.bukkit.Material;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ToolUtils {

    private static final List<Material> weapon = List.of(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD, Material.TRIDENT, Material.BOW, Material.CROSSBOW);
    private static final List<Material> pickaxe = List.of(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE);
    private static final List<Material> axe = List.of(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);
    private static final List<Material> shovel = List.of(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL);
    private static final List<Material> hoe = List.of(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE);

    private static final List<Material> wooden = List.of(Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE, Material.WOODEN_AXE, Material.WOODEN_SWORD);
    private static final List<Material> stone = List.of(Material.STONE_PICKAXE, Material.STONE_SHOVEL, Material.STONE_HOE, Material.STONE_AXE, Material.STONE_SWORD);
    private static final List<Material> iron = List.of(Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_HOE, Material.IRON_AXE, Material.IRON_SWORD);
    private static final List<Material> golden = List.of(Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE, Material.GOLDEN_AXE, Material.GOLDEN_SWORD);
    private static final List<Material> diamond = List.of(Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE, Material.DIAMOND_AXE, Material.DIAMOND_SWORD);
    private static final List<Material> netherite = List.of(Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_HOE, Material.NETHERITE_AXE, Material.NETHERITE_SWORD);

    public static boolean isWeapon(Material material) {
        return weapon.contains(material);
    }

    public static boolean isPickaxe(Material material) {
        return pickaxe.contains(material);
    }

    public static boolean isAxe(Material material) {
        return axe.contains(material);
    }

    public static boolean isShovel(Material material) {
        return shovel.contains(material);
    }

    public static boolean isHoe(Material material) {
        return hoe.contains(material);
    }

    public static boolean isWooden(Material material) {
        return wooden.contains(material);
    }

    public static boolean isStone(Material material) {
        return stone.contains(material);
    }

    public static boolean isIron(Material material) {
        return iron.contains(material);
    }

    public static boolean isGolden(Material material) {
        return golden.contains(material);
    }

    public static boolean isDiamond(Material material) {
        return diamond.contains(material);
    }

    public static boolean isNetherite(Material material) {
        return netherite.contains(material);
    }
}