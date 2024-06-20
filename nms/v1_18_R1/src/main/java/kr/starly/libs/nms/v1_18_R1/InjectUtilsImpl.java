package kr.starly.libs.nms.v1_18_R1;

import io.netty.channel.Channel;
import kr.starly.libs.nms.abstraction.util.InjectUtils;
import kr.starly.libs.nms.reflect.resolver.FieldResolver;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class InjectUtilsImpl implements InjectUtils {

    @Override
    public Object getServerConnection() {
        return ((CraftServer) Bukkit.getServer()).getServer().getConnection();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List<Object> getServerConnectionChannels() {
        ServerConnectionListener serverConnection = ((CraftServer) Bukkit.getServer()).getServer().getConnection();
        if (serverConnection == null) return null;

        return (List) serverConnection.getConnections();
    }

    @Override
    public Channel getChannel(Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        Connection manager = new FieldResolver(connection.getClass()).resolveAccessor("h").get(connection);

        return manager.channel;
    }

    @Override
    public String parsePlayerName(Object rawPacket) {
        if (rawPacket instanceof ServerboundHelloPacket packet) {
            return packet.getGameProfile().getName();
        }

        return null;
    }
}