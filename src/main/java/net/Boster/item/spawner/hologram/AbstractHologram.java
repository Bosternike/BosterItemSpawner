package net.Boster.item.spawner.hologram;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import lombok.Getter;
import net.Boster.item.spawner.BosterItemSpawner;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractHologram {

    @Getter @NotNull protected final Location location;
    @Getter @NotNull protected final CraftHologram hologram;
    @Getter @NotNull protected final List<String> lines;
    @Nullable protected BukkitTask task;

    public AbstractHologram(Location loc, @NotNull List<String> lines) {
        this.location = loc;
        this.lines = lines;
        this.hologram = (CraftHologram) HologramsAPI.createHologram(BosterItemSpawner.getInstance(), loc.clone());
    }

    public void run() {
        run(0, 1);
    }

    public void run(int delay, int period) {
        stopTask();

        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(BosterItemSpawner.getInstance(), delay, period);
    }

    public void stopTask() {
        if(task != null) {
            task.cancel();
            task = null;
        }
    }

    public boolean isRunning() {
        return task != null;
    }

    public abstract void tick();

    public void remove() {
        hologram.delete();
    }
}
