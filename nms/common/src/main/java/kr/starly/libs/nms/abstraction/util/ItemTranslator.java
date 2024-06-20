package kr.starly.libs.nms.abstraction.util;

import com.google.gson.Gson;
import kr.starly.libs.nms.version.NmsRevision;
import org.bukkit.inventory.ItemStack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

public interface ItemTranslator {

    String getTranslationKey(ItemStack itemStack);

    // Locale.KOREA
    // Locale.US
    // Locale.JAPAN
    // Locale.SIMPLIFIED_CHINESE
    @SuppressWarnings("unchecked")
    default String translateItemName(ItemStack itemStack, Locale locale) {
        NmsRevision revision = NmsRevision.REQUIRED_REVISION;
        String resourcePath = "/assets/lang/%d_%d/%s_%s.json"
                .formatted(revision.getSince()[0], revision.getSince()[1], locale.getLanguage(), locale.getCountry().toLowerCase());

        InputStream is = ItemTranslator.class.getResourceAsStream(resourcePath);
        if (is == null) return "UNTRANSLATED";

        Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        HashMap<String, String> languageMap = gson.fromJson(reader, HashMap.class);
        return languageMap.get(getTranslationKey(itemStack));
    }
}