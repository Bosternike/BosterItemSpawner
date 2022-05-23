package net.Boster.item.spawner.libs;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.spawner.ItemSpawner;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PAPIExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "bosteritemspawner";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Bosternike";
    }

    @Override
    public @NotNull String getVersion() {
        return BosterItemSpawner.getInstance().getDescription().getVersion();
    }


    @Override
    public String onRequest(@NotNull OfflinePlayer p, @NotNull String params) {
        if(!params.contains("_")) return "too few arguments";

        String[] ss = params.split("_");
        if(ss.length < 2) return "too few arguments";

        ItemSpawner spawner = ItemSpawner.get(ss[0]);
        String arg = ss[1];

        if(spawner == null) {
            return "Spawner " + ss[0] + " could not be found.";
        }

        if(arg.equalsIgnoreCase("delay")) {
            return Integer.toString(spawner.spawnDelay);
        } else if(arg.equalsIgnoreCase("remaining")) {
            return Integer.toString(spawner.spawnDelay - spawner.ticked);
        } else if(arg.equalsIgnoreCase("sif")) {
            return spawner.spawnerCounter.getAsString();
        } else {
            return "Invalid argument " + arg;
        }
    }
}
