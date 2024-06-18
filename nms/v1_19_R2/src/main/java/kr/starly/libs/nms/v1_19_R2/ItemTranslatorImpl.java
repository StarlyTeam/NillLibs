package kr.starly.libs.nms.v1_19_R2;

import kr.starly.libs.nms.abstraction.util.ItemTranslator;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemTranslatorImpl implements ItemTranslator {

    @Override
    public String getTranslationKey(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack).getDescriptionId();
    }
}