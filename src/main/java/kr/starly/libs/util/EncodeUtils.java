package kr.starly.libs.util;

import lombok.NoArgsConstructor;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import static lombok.AccessLevel.PRIVATE;

@SuppressWarnings("unchecked")
@NoArgsConstructor(access = PRIVATE)
public class EncodeUtils {

    public static String serialize(Object object) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(object);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T> T deserialize(String serializedItemStack, Class<T> type) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(serializedItemStack));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            return (T) dataInput.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}