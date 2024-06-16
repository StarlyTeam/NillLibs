package kr.starly.libs.protocol.listener;

import kr.starly.libs.protocol.event.PacketReceiveEvent;
import kr.starly.libs.protocol.event.PacketSendEvent;

public interface PacketListener {

    default void onSend(PacketSendEvent event) {
    }

    default void onReceive(PacketReceiveEvent event) {
    }
}