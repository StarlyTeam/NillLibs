package kr.starly.libs.protocol;

import com.google.common.collect.MapMaker;
import io.netty.channel.*;
import kr.starly.libs.nms.NmsMultiVersion;
import kr.starly.libs.nms.reflect.resolver.FieldResolver;
import kr.starly.libs.protocol.event.PacketReceiveEvent;
import kr.starly.libs.protocol.event.PacketSendEvent;
import kr.starly.libs.scheduler.Do;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
public class TinyProtocol {

    private final Plugin plugin;
    private final @Getter PacketListenerManager packetListenerManager;
    private Listener listener;
    protected volatile boolean closed;

    private final Map<String, Channel> channelLookup = new MapMaker().weakValues().makeMap();
    private List<Object> networkManagers;
    private final Set<Channel> uninjectedChannels = Collections.newSetFromMap(new MapMaker().weakKeys().<Channel, Boolean>makeMap());
    private final List<Channel> serverChannels = new ArrayList<>();

    private ChannelInboundHandlerAdapter serverChannelHandler;
    private ChannelInitializer<Channel> beginInitProtocol;
    private ChannelInitializer<Channel> endInitProtocol;

    public TinyProtocol(Plugin plugin) {
        this.plugin = plugin;
        this.packetListenerManager = new PacketListenerManager();

        registerBukkitEvents();

        try {
            registerChannelHandler();
            registerPlayers(plugin);
        } catch (IllegalArgumentException ex) {
            Do.sync(() -> {
                registerChannelHandler();
                registerPlayers(plugin);
            });
        }
    }

    private void createServerChannelHandler() {
        endInitProtocol = new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                try {
                    synchronized (networkManagers) {
                        if (!closed) {
                            channel.eventLoop().submit(() -> injectChannelInternal(channel));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        beginInitProtocol = new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline().addLast(endInitProtocol);
            }
        };

        serverChannelHandler = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                Channel channel = (Channel) msg;

                channel.pipeline().addFirst(beginInitProtocol);
                ctx.fireChannelRead(msg);
            }
        };
    }

    private void registerBukkitEvents() {
        listener = new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onPlayerLogin(PlayerLoginEvent e) {
                if (closed) return;

                Channel channel = getChannel(e.getPlayer());
                if (!uninjectedChannels.contains(channel)) {
                    injectPlayer(e.getPlayer());
                }
            }

            @EventHandler
            public void onPluginDisable(PluginDisableEvent e) {
                if (e.getPlugin().equals(plugin)) {
                    close();
                }
            }
        };

        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    private void registerChannelHandler() {
        networkManagers = NmsMultiVersion.getInjectUtils().getServerConnectionChannels();
        if (networkManagers == null) {
            throw new IllegalArgumentException("Failed to obtain list of network managers");
        }

        createServerChannelHandler();

        boolean looking = true;
        while (looking) {
            Object serverConnection = NmsMultiVersion.getInjectUtils().getServerConnection();
            List<ChannelFuture> channels = new FieldResolver(serverConnection.getClass()).resolveAccessor("channels").get(serverConnection);

            for (ChannelFuture item : channels) {
                if (item == null) break;

                Channel serverChannel = item.channel();
                serverChannels.add(serverChannel);
                serverChannel.pipeline().addFirst(serverChannelHandler);

                looking = false;
            }
        }
    }

    private void unregisterChannelHandler() {
        if (serverChannelHandler == null) return;

        for (Channel serverChannel : serverChannels) {
            ChannelPipeline pipeline = serverChannel.pipeline();

            serverChannel.eventLoop().execute(() -> {
                try {
                    pipeline.remove(serverChannelHandler);
                } catch (NoSuchElementException ignored) {}
            });
        }
    }

    private void registerPlayers(Plugin plugin) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            injectPlayer(player);
        }
    }

    public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
        PacketSendEvent event = new PacketSendEvent(receiver, channel, packet);
        packetListenerManager.callEvent(event);

        return event.isCancelled() ? null : event.getPacket();
    }

    public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
        PacketReceiveEvent event = new PacketReceiveEvent(sender, channel, packet);
        packetListenerManager.callEvent(event);

        return event.isCancelled() ? null : event.getPacket();
    }

    public void sendPackets(Player player, Object... packets) {
        sendPackets(getChannel(player), packets);
    }

    public void sendPackets(Channel channel, Object... packets) {
        for (Object packet : packets) {
            channel.pipeline().writeAndFlush(packet);
        }
    }

    public void receivePacket(Player player, Object packet) {
        receivePacket(getChannel(player), packet);
    }

    public void receivePacket(Channel channel, Object packet) {
        channel.pipeline().context("encoder").fireChannelRead(packet);
    }

    public void injectPlayer(Player player) {
        injectChannelInternal(getChannel(player)).player = player;
    }

    public void injectChannel(Channel channel) {
        injectChannelInternal(channel);
    }

    private PacketInterceptor injectChannelInternal(Channel channel) {
        try {
            PacketInterceptor interceptor = (PacketInterceptor) channel.pipeline().get("TinyProtocol");
            if (interceptor == null) {
                interceptor = new PacketInterceptor();
                channel.pipeline().addBefore("packet_handler", "TinyProtocol", interceptor);
                uninjectedChannels.remove(channel);
            }

            return interceptor;
        } catch (IllegalArgumentException ignored) {
            return (PacketInterceptor) channel.pipeline().get("TinyProtocol");
        }
    }

    public Channel getChannel(Player player) {
        Channel channel = channelLookup.get(player.getName());
        if (channel == null) {
            channelLookup.put(player.getName(), channel = NmsMultiVersion.getInjectUtils().getChannel(player));
        }

        return channel;
    }

    public void uninjectPlayer(Player player) {
        uninjectChannel(getChannel(player));
    }

    public void uninjectChannel(final Channel channel) {
        if (!closed) {
            uninjectedChannels.add(channel);
        }

        channel.eventLoop().execute(() -> channel.pipeline().remove("TinyProtocol"));
    }

    public boolean hasInjected(Player player) {
        return hasInjected(getChannel(player));
    }

    public boolean hasInjected(Channel channel) {
        return channel.pipeline().get("TinyProtocol") != null;
    }

    public final void close() {
        if (!closed) {
            closed = true;

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                uninjectPlayer(player);
            }

            HandlerList.unregisterAll(listener);
            unregisterChannelHandler();
        }
    }

    private final class PacketInterceptor extends ChannelDuplexHandler {

        public volatile Player player;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Channel channel = ctx.channel();
            handleLoginStart(channel, msg);

            try {
                msg = onPacketInAsync(player, channel, msg);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.SEVERE, "Error in onPacketInAsync().", ex);
            }

            if (msg != null) {
                super.channelRead(ctx, msg);
            }
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            try {
                msg = onPacketOutAsync(player, ctx.channel(), msg);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.SEVERE, "Error in onPacketOutAsync().", ex);
            }

            if (msg != null) {
                super.write(ctx, msg, promise);
            }
        }

        private void handleLoginStart(Channel channel, Object rawPacket) {
            String playerName = NmsMultiVersion.getInjectUtils().parsePlayerName(rawPacket);
            channelLookup.put(playerName, channel);
        }
    }
}