package kr.starly.libs.nms.abstraction.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.bukkit.entity.Player;

import java.util.List;

public interface InjectUtils {

    Object getServerConnection();

    List<Object> getServerConnections();

    List<ChannelFuture> getServerChannels();

    Channel getChannel(Player player);

    String parsePlayerName(Object serverBoundHelloPacket);
}