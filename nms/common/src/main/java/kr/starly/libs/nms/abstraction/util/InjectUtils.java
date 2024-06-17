package kr.starly.libs.nms.abstraction.util;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.util.List;

public interface InjectUtils {

    Object getServerConnection();

    List<Object> getServerConnectionChannels();

    Channel getChannel(Player player);

    String parsePlayerName(Object serverBoundHelloPacket);
}