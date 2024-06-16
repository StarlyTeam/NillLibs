package kr.starly.libs.protocol.listener;

import kr.starly.libs.protocol.event.PacketReceiveEvent;

@FunctionalInterface
public interface PacketReceiveListener extends PacketListener {

    @Override
    void onReceive(PacketReceiveEvent event);
}