package net.Boster.item.spawner.hologram.counter;

import net.Boster.item.spawner.hologram.ItemSpawner;

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
        for(int i = 0; i < maxAmount - Math.max(0, spawner.ticked * (maxAmount / spawner.spawnDelay)); i++) {
            r += format;
        }
        str = r;
    }

    @Override
    public void refresh() {
        str = "";
        for(int i = 0; i < maxAmount; i++) {
            str += format;
        }
    }

    @Override
    public String getAsString() {
        return str;
    }
}
