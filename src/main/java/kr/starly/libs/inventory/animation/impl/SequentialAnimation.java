package kr.starly.libs.inventory.animation.impl;

import java.util.List;

public class SequentialAnimation extends AbstractSoundAnimation {

    public SequentialAnimation(int tickDelay, boolean sound) {
        super(tickDelay, sound);
    }

    @Override
    protected void handleFrame(int frame) {
        List<Integer> slots = getSlots();
        if (!slots.isEmpty()) {
            show(slots.getFirst());
            slots.removeFirst();
        } else finish();
    }
}