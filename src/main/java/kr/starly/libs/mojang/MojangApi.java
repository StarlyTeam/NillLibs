package kr.starly.libs.mojang;

import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

public class MojangApi {

    public static UUID fetchUuid(String username) {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;

        try {
            URL api = new URI(url).toURL();
            Scanner scanner = new Scanner(api.openStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JSONObject jsonResponse = new JSONObject(response);
            String uuid = jsonResponse.getString("id");
            uuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20);
            return UUID.fromString(uuid);
        } catch (Exception ignored) {}

        return null;
    }

    public static String fetchUsername(UUID uuid) {
        String uuidWithoutDashes = uuid.toString().replace("-", "");
        String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuidWithoutDashes;

        try {
            URL api = new URI(url).toURL();
            Scanner scanner = new Scanner(api.openStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getString("name");
        } catch (Exception ignored) {}

        return null;
    }
}