package net.Boster.item.spawner.holo.line;

import org.jetbrains.annotations.Nullable;

public interface HoloTextLine extends HoloLine {

    @Nullable String getText();
    void setText(@Nullable String text);
}
