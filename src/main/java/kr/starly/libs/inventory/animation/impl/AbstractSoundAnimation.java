package kr.starly.libs.inventory.animation.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
@Setter
public abstract class AbstractSoundAnimation extends AbstractAnimation {

    private String sound = Sound.ENTITY_ITEM_PICKUP.getKey().getKey();
    private float volume = 1;
    private float pitch = 1;

    public AbstractSoundAnimation(int tickDelay, boolean sound) {
        super(tickDelay);

        if (sound) {
            addShowHandler((frame, index) -> {
                        for (Player viewer : getCurrentViewers()) {
                            viewer.playSound(viewer.getLocation(), this.sound, volume, pitch);
                        }
                    }
            );
        }
    }
}