package kr.starly.libs.nms.version;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class VersionUtils {

    private static final int major;
    private static final int minor;
    private static final int patch;

    static {
        String version = Bukkit.getVersion();
        version = version.substring(version.indexOf(':') + 2, version.lastIndexOf(')'));

        int[] parts = toMajorMinorPatch(version);
        major = parts[0];
        minor = parts[1];
        patch = parts[2];
    }

    public static int[] toMajorMinorPatch(String version) {
        String[] parts = version.split("\\.");

        int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

        return new int[] {major, minor, patch};
    }

    public static boolean isHigherOrEqualThanServer(String version) {
        return isHigherOrEqualThanServer(toMajorMinorPatch(version));
    }

    public static boolean isHigherOrEqualThanServer(int... version) {
        if (version.length != 3)
            throw new IllegalArgumentException("Version array must have a size of 3");

        return version[0] > major
                || (version[0] == major && version[1] > minor)
                || (version[0] == major && version[1] == minor && version[2] >= patch);
    }

    public static boolean isServerHigherOrEqual(String version) {
        return isServerHigherOrEqual(toMajorMinorPatch(version));
    }

    public static boolean isServerHigherOrEqual(int... version) {
        if (version.length != 3)
            throw new IllegalArgumentException("Version array must have a size of 3");

        return major > version[0]
                || (major == version[0] && minor > version[1])
                || (major == version[0] && minor == version[1] && patch >= version[2]);
    }
}