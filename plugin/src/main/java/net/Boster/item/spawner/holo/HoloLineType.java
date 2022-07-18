package net.Boster.item.spawner.holo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HoloLineType {

    TEXT(0.27),
    ITEM(0.4);

    @Getter private final double height;
}
