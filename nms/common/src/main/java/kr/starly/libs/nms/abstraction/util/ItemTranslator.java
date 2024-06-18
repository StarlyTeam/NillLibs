package kr.starly.libs.nms.abstraction.util;

import kr.starly.libs.nms.version.NmsRevision;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Locale;

public interface ItemTranslator {

    String getTranslationKey(ItemStack itemStack);

    // Locale.KOREA
    // Locale.US
    // Locale.JAPAN
    // Locale.SIMPLIFIED_CHINESE
    default String translateItemName(ItemStack itemStack, Locale locale) {
        NmsRevision revision = NmsRevision.REQUIRED_REVISION;
        String resourcePath = "/assets/lang/%d_%d/%s_%s.json"
                .formatted(revision.getSince()[0], revision.getSince()[1], locale.getLanguage(), locale.getCountry());

        InputStream is = ItemTranslator.class.getResourceAsStream(resourcePath);
        if (is == null) return "UNTRANSLATED";

        return new JSONObject(is).getString(getTranslationKey(itemStack));
    }
}