package kr.starly.libs.protocol.event;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public final class PacketReceiveEvent extends PacketEvent {

    private final Player player;
    private final Channel channel;
    @Setter
    private Object packet;

    public PacketReceiveEvent(@Nullable Player player, Channel channel, Object packet) {
        this.player = player;
        this.channel = Objects.requireNonNull(channel, "Channel is null.");
        this.packet = Objects.requireNonNull(packet, "Packet is null.");
    }
}