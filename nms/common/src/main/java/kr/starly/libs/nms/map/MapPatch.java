package kr.starly.libs.nms.map;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class MapPatch implements Serializable {

    private final int startX;
    private final int startY;
    private final int width;
    private final int height;
    private final byte[] colors;
}