package net.Boster.item.spawner.holo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HoloLineType {

    TEXT(0.30),
    ITEM(0.5);

    @Getter private final double height;
}
