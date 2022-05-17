package net.Boster.item.spawner.manager;

import net.Boster.item.spawner.spawner.AbstractSpawner;
import org.jetbrains.annotations.NotNull;

public interface TaskManager {

    void start();
    void stop();
    void addSpawner(@NotNull AbstractSpawner spawner);
    void removeSpawner(@NotNull AbstractSpawner spawner);
    boolean hasSpawner(@NotNull AbstractSpawner spawner);
    void clear();
    boolean isRunning();
    boolean isAsync();
}
