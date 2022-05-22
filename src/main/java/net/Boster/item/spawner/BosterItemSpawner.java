package net.Boster.item.spawner;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.Boster.item.spawner.commands.Commands;
import net.Boster.item.spawner.libs.PAPISupport;
import net.Boster.item.spawner.listeners.ChunkListener;
import net.Boster.item.spawner.manager.SpawnersManager;
import net.Boster.item.spawner.utils.ConfigUtils;
import net.Boster.item.spawner.utils.LogType;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStreamReader;

public class BosterItemSpawner extends JavaPlugin {

    @Getter private static BosterItemSpawner instance;

    @Getter @Nullable private BukkitTask updaterTask;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        updateConfigIfNeeded();

        PAPISupport.load();

        getCommand("bosteritemspawner").setExecutor(new Commands(this));
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);

        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            Bukkit.getConsoleSender().sendMessage("\u00a76+\u00a7a---------------- \u00a7dBosterItemSpawner \u00a7a------------------\u00a76+");
            SpawnersManager.enable();
            Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBosterItemSpawner\u00a7d] \u00a7fThe plugin has been \u00a7dEnabled\u00a7f!");
            Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBosterItemSpawner\u00a7d] \u00a7fPlugin creator: \u00a7dBosternike");
            Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBosterItemSpawner\u00a7d] \u00a7fPlugin creator URL: \u00a7dvk.com/bosternike");
            Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBosterItemSpawner\u00a7d] \u00a7fPlugin version: \u00a7d" + getDescription().getVersion());
            Bukkit.getConsoleSender().sendMessage("\u00a76+\u00a7a---------------- \u00a7dBosterItemSpawner \u00a7a------------------\u00a76+");

            prepareUpdater();
        }, 10);
    }

    public void onDisable() {
        SpawnersManager.disable();
    }

    public void reload() {
        reloadConfig();

        SpawnersManager.disable();
        SpawnersManager.enable();
    }

    public void prepareUpdater() {
        if(getConfig().getBoolean("Updater.Enabled", false)) {
            enableUpdater(getConfig().getInt("Updater.Delay", 3600));
        }
    }

    public void enableUpdater(int delay) {
        if(updaterTask != null) {
            updaterTask.cancel();
        }

        UpdateChecker c = new UpdateChecker(this, 102147);
        updaterTask = getServer().getScheduler().runTaskTimer(this, () -> {
            c.getVersion(version -> {
                if(!getDescription().getVersion().equals(version)) {
                    log("There is a new update available!", LogType.UPDATER);
                    log("Current version:&c " + getDescription().getVersion(), LogType.UPDATER);
                    log("New version:&b " + version, LogType.UPDATER);
                    log("Download link:&a https://www.spigotmc.org/resources/bosterparticles.100933/", LogType.UPDATER);
                }
            });
        }, 0, delay * 20L);
    }

    public void log(String s, LogType log) {
        Bukkit.getConsoleSender().sendMessage(log.getFormat() + log.getColor() + Utils.toColor(s));
    }

    private void updateConfigIfNeeded() {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("config.yml")));
        File cf = new File(getDataFolder(), "config.yml");
        if(!ConfigUtils.hasAllStrings(cfg, YamlConfiguration.loadConfiguration(cf), ImmutableList.of())) {
            ConfigUtils.replaceOldConfig(cf, cf, getResource("config.yml"));
        }
    }
}
