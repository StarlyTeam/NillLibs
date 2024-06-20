package kr.starly.libs.nms.v1_19_R1;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import kr.starly.libs.nms.abstraction.util.InjectUtils;
import kr.starly.libs.nms.reflect.resolver.FieldResolver;
import kr.starly.libs.nms.reflect.util.AccessUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

public class InjectUtilsImpl implements InjectUtils {

    @Override
    public Object getServerConnection() {
        return ((CraftServer) Bukkit.getServer()).getServer().getConnection();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List<Object> getServerConnections() {
        ServerConnectionListener serverConnection = ((CraftServer) Bukkit.getServer()).getServer().getConnection();
        if (serverConnection == null) return null;

        return (List) serverConnection.getConnections();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ChannelFuture> getServerChannels() {
        ServerConnectionListener serverConnection = ((CraftServer) Bukkit.getServer()).getServer().getConnection();
        if (serverConnection == null) return null;

        Field field = new FieldResolver(serverConnection.getClass()).resolveSilent("f");
        AccessUtil.setAccessible(field);

        try {
            return (List<ChannelFuture>) field.get(serverConnection);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
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
            String playerName = packet.name();
            if (playerName == null || playerName.isEmpty()) return null;

            return playerName.substring(0, Math.min(16, playerName.length()));
        }

        return null;
    }
}