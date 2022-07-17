package net.Boster.item.spawner.holo.entity;

import org.jetbrains.annotations.NotNull;

public interface NMSEntity {

    int getId();
    void destroy();
    @NotNull Object getObject();
}
