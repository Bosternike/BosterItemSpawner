package net.Boster.item.spawner.manager;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.spawner.AbstractSpawner;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class DefaultTaskManager implements TaskManager {

    private final LinkedList<AbstractSpawner> spawners = new LinkedList<>();
    private BukkitTask task;

    @Override
    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for(AbstractSpawner spawner : spawners) {
                    spawner.setTicksCounted(spawner.getTicksCounted() + 1);
                    if(spawner.getTicksCounted() >= spawner.getDelayTicksAmount()) {
                        spawner.setTicksCounted(0);
                        spawner.tick();
                    }
                }
            }
        }.runTaskTimerAsynchronously(BosterItemSpawner.getInstance(), 0, 0);
    }

    @Override
    public void stop() {
        if(task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void addSpawner(@NotNull AbstractSpawner spawner) {
        spawners.add(spawner);
    }

    @Override
    public void removeSpawner(@NotNull AbstractSpawner spawner) {
        spawners.remove(spawner);
    }

    @Override
    public boolean hasSpawner(@NotNull AbstractSpawner spawner) {
        return spawners.contains(spawner);
    }

    @Override
    public void clear() {
        spawners.clear();
    }

    @Override
    public boolean isRunning() {
        return task != null;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
