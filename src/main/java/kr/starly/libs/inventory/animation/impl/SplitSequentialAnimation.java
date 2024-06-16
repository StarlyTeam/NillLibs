package kr.starly.libs.inventory.animation.impl;

import java.util.List;

public class SplitSequentialAnimation extends AbstractSoundAnimation {

    public SplitSequentialAnimation(int tickDelay, boolean sound) {
        super(tickDelay, sound);
    }

    @Override
    protected void handleFrame(int frame) {
        List<Integer> slots = getSlots();

        int i = slots.getFirst();
        int i2 = slots.getLast();

        show(i, i2);

        if (slots.size() <= 2) {
            finish();
            return;
        }

        slots.removeFirst();
        slots.removeLast();
    }
}