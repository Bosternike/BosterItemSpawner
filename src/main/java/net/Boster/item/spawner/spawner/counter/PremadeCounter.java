package net.Boster.item.spawner.spawner.counter;

import net.Boster.item.spawner.spawner.ItemSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class PremadeCounter implements SpawnerCounter {

    private final ItemSpawner spawner;
    private final HashMap<Integer, String> map = new HashMap<>();
    private String maxStr;
    private String str;

    public PremadeCounter(ItemSpawner spawner) {
        this.spawner = spawner;

        ConfigurationSection c = spawner.file.getFile().getConfigurationSection("CounterFormat.PremadeCounter");
        if(c != null) {
            int maxId = 0;
            for(String s : c.getKeys(false)) {
                try {
                    int i = Integer.parseInt(s);
                    map.put(i, c.getString(s));
                    maxId = Math.max(maxId, i);
                } catch (Exception ignored) {}
            }

            maxStr = map.get(maxId);
        }
    }

    @Override
    public void secondRun() {
        str = map.getOrDefault(spawner.spawnDelay - spawner.ticked, maxStr);
    }

    @Override
    public void refresh() {
        str = maxStr;
    }

    @Override
    @NotNull
    public String getAsString() {
        return str;
    }
}
