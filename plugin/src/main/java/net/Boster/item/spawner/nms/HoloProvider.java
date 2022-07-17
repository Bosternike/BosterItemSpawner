package net.Boster.item.spawner.nms;

import net.Boster.item.spawner.holo.entity.NMSEntity;
import net.Boster.item.spawner.holo.line.HoloLine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface HoloProvider {

    NMSEntity createLineEntity(@NotNull HoloLine line);
    void show(@NotNull HoloLine line, @NotNull Collection<? extends Player> players);
}
