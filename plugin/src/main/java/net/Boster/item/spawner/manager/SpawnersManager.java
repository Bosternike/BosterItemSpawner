package net.Boster.item.spawner.manager;

import lombok.Getter;
import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.files.SupportFile;
import net.Boster.item.spawner.spawner.ItemSpawner;
import net.Boster.item.spawner.utils.LogType;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpawnersManager {

    @Getter @NotNull private static TaskManager taskManager = new DefaultTaskManager();

    public static void enable() {
        loadFiles();
        for(SpawnerFile f : SpawnerFile.getValues()) {
            loadSpawner(f);
        }
        taskManager.start();
    }

    public static void disable() {
        try {
            taskManager.stop();
            taskManager.clear();
            for(ItemSpawner h : ItemSpawner.spawners()) {
                h.clearDrops();
                h.removeHolograms();
            }
            SpawnerFile.clearAll();
        } catch (NoClassDefFoundError ignored) {}
    }

    public static void setTaskManager(@NotNull TaskManager manager, boolean run) {
        if(taskManager.isRunning()) {
            taskManager.stop();
        }

        taskManager = manager;
        if(run) {
            taskManager.start();
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

    public static boolean loadSpawner(@NotNull SpawnerFile f) {
        Location loc = Utils.getLocation(f.getFile().getString("location"));
        if(loc != null) {
            ItemSpawner s = new ItemSpawner(f, f.getName(), loc, f.getFile().getStringList("Hologram"));
            s.setDelayTicksAmount(f.getFile().getInt("DelayTicksAmount", 20));
            s.spawnDelay = f.getFile().getInt("SpawnDelay", 20);
            try {
                s.item = Utils.deserializeItem(f.getFile().getString("item"));
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
