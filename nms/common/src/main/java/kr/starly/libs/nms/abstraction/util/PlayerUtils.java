package kr.starly.libs.nms.abstraction.util;

import kr.starly.libs.nms.map.MapIcon;
import kr.starly.libs.nms.map.MapPatch;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PlayerUtils {

    void stopAdvancementListening(@NotNull Player player);

    void stopAdvancementListening(@NotNull Object player);

    void startAdvancementListening(@NotNull Player player);

    void startAdvancementListening(@NotNull Object player);

    void sendMapUpdate(@NotNull Player player, int mapId, byte scale, boolean locked, @Nullable MapPatch mapPatch, @Nullable List<MapIcon> icons);
}