package net.Boster.item.spawner.holo.entity;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface NMSEntity {

    int getId();
    void destroy();
    @NotNull Object getObject();
    @NotNull List<Player> getViewers();
}
