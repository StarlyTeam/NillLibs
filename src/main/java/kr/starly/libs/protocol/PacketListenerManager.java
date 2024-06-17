package kr.starly.libs.protocol;

import kr.starly.libs.protocol.event.PacketEvent;
import kr.starly.libs.protocol.event.PacketReceiveEvent;
import kr.starly.libs.protocol.event.PacketSendEvent;
import kr.starly.libs.protocol.listener.PacketListener;

import java.util.*;

public final class PacketListenerManager {

    private final Map<PacketListenerPriority, Set<PacketListener>> listenersMap =
            Collections.synchronizedMap(new EnumMap<>(PacketListenerPriority.class));

    public PacketListener registerPacketListener(PacketListenerPriority priority, PacketListener listener) {
        Set<PacketListener> list = listenersMap.computeIfAbsent(priority, (k) -> new HashSet<>());
        list.add(listener);

        return listener;
    }

    public void unregisterPacketListener(PacketListener listener) {
        listenersMap.entrySet().removeIf((l) -> l.getValue().remove(listener) && l.getValue().isEmpty());
    }

    void callEvent(PacketEvent event) {
        for (PacketListenerPriority priority : PacketListenerPriority.values()) {
            Set<PacketListener> listeners = listenersMap.get(priority);
            if (listeners == null) return;

            listeners.forEach(listener -> {
                if (event instanceof PacketSendEvent sendEvent) {
                    listener.onSend(sendEvent);
                } else if (event instanceof PacketReceiveEvent receiveEvent) {
                    listener.onReceive(receiveEvent);
                }
            });
        }
    }
}