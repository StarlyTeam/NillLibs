package kr.starly.libs.nms.component;

import org.jetbrains.annotations.NotNull;

public interface ComponentWrapper extends Cloneable {

    @NotNull String serializeToJson();

    @NotNull ComponentWrapper withoutPreFormatting();

    @NotNull ComponentWrapper clone();
}