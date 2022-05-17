package net.Boster.item.spawner.spawner.counter;

import org.jetbrains.annotations.NotNull;

public interface SpawnerCounter {

    void secondRun();

    void refresh();

    @NotNull String getAsString();
}
