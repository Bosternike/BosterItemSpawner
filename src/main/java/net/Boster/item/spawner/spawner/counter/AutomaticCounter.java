package net.Boster.item.spawner.spawner.counter;

import net.Boster.item.spawner.spawner.ItemSpawner;
import org.jetbrains.annotations.NotNull;

public class AutomaticCounter implements SpawnerCounter {

    private final ItemSpawner spawner;
    private final int maxAmount;
    private final String format;
    private String str = "";

    public AutomaticCounter(ItemSpawner spawner) {
        this.spawner = spawner;
        this.maxAmount = spawner.file.getFile().getInt("CounterFormat.AutomaticCounter.MaxAmount", 20);
        this.format = spawner.file.getFile().getString("CounterFormat.AutomaticCounter.Format", "");
    }

    @Override
    public void secondRun() {
        String r = "";
        int m = Math.min(spawner.spawnDelay, maxAmount);
        for(int i = 0; i < m - Math.max(0, spawner.ticked * ((double) m / (double) spawner.spawnDelay)); i++) {
            r += format;
        }
        str = r;
    }

    @Override
    public void refresh() {
        str = "";
        for(int i = 0; i < Math.min(spawner.spawnDelay, maxAmount); i++) {
            str += format;
        }
    }

    @Override
    @NotNull
    public String getAsString() {
        return str;
    }
}
