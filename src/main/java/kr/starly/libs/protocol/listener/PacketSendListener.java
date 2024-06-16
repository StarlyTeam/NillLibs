package kr.starly.libs.protocol.listener;

import kr.starly.libs.protocol.event.PacketSendEvent;

@FunctionalInterface
public interface PacketSendListener extends PacketListener {

    @Override
    void onSend(PacketSendEvent event);
}