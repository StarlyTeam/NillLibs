package kr.starly.libs.protocol.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public abstract class PacketEvent implements Cancellable {

    private boolean cancelled = false;
}