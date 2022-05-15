package net.Boster.item.spawner.manager;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.files.SupportFile;
import net.Boster.item.spawner.hologram.ItemSpawner;
import net.Boster.item.spawner.utils.LogType;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpawnersManager {

    public static void enable() {
        loadFiles();
        for(SpawnerFile f : SpawnerFile.getValues()) {
            loadSpawner(f);
        }
    }

    private static void loadFiles() {
        File nf = new File(BosterItemSpawner.getInstance().getDataFolder() + "/spawners");
        File[] ls = nf.listFiles();
        if(ls != null) {
            if(ls.length > 0) {
                for(File fl : ls) {
                    if(!fl.getName().endsWith(".yml")) continue;

                    SpawnerFile file = new SpawnerFile(fl);
                    file.create();
                }
            }
        }
    }

    public static void disable() {
        try {
            for(ItemSpawner h : ItemSpawner.holograms()) {
                h.stopTask();
                h.clearDrops();
                h.removeHolograms();
            }
            SpawnerFile.clearAll();
        } catch (NoClassDefFoundError ignored) {}
    }

    public static boolean loadSpawner(@NotNull SpawnerFile f) {
        Location loc = Utils.getLocation(f.getFile().getString("location"));
        if(loc != null) {
            ItemSpawner s = new ItemSpawner(f, f.getName(), loc, f.getFile().getStringList("Hologram"));
            s.delayTicksAmount = f.getFile().getInt("DelayTicksAmount", 20);
            s.spawnDelay = f.getFile().getInt("SpawnDelay", 20);
            try {
                s.item = f.getFile().getItemStack("item");
            } catch (Exception e) {
                return false;
            }
            s.spawnLimitEnabled = f.getFile().getBoolean("SpawnLimit.Enabled", false);
            s.spawnLimitAmount = f.getFile().getInt("SpawnLimit.Amount", 3);
            s.spawnLimitDisableCounter = f.getFile().getBoolean("SpawnLimit.DisableCounterOnFull", true);
            s.loadShownItem();
            s.start();
            return true;
        } else {
            BosterItemSpawner.getInstance().log("&7Could not load spawner \"&c" + f.getName() + "&7\". Location is null!", LogType.WARNING);
            return false;
        }
    }

    public static void createSpawner(@NotNull String s, @Nullable Location loc, @Nullable String source) {
        SupportFile file = new SupportFile(s);
        file.setDirectory("spawners/");
        file.createNewFile();
        file.getConfiguration().addDefaults(BosterItemSpawner.getInstance().getConfig().getConfigurationSection("Defaults").getValues(true));
        file.getConfiguration().options().copyDefaults(true);
        if(loc != null) {
            file.getConfiguration().set("location", Utils.locationToString(loc, true));
        }
        file.getConfiguration().set("source", source == null ? "Unknown" : source);
        file.save();
        file.clear();
        SpawnerFile af = new SpawnerFile(file.getFile());
        af.create();
    }
}
