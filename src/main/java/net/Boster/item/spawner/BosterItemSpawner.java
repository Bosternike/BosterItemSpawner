package net.Boster.item.spawner;

import lombok.Getter;
import net.Boster.item.spawner.commands.Commands;
import net.Boster.item.spawner.listeners.Events;
import net.Boster.item.spawner.listeners.pickup.NewPickupListener;
import net.Boster.item.spawner.listeners.pickup.OldPickupListener;
import net.Boster.item.spawner.manager.SpawnersManager;
import net.Boster.item.spawner.utils.LogType;
import net.Boster.item.spawner.utils.Utils;
import net.Boster.item.spawner.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BosterItemSpawner extends JavaPlugin {

    @Getter private static BosterItemSpawner instance;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        getCommand("bosteritemspawner").setExecutor(new Commands(this));

        registerListeners();

        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            Bukkit.getConsoleSender().sendMessage("\u00a76+\u00a7a---------------- \u00a7dBosterItemSpawner \u00a7a------------------\u00a76+");
            SpawnersManager.enable();
            Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBosterItemSpawner\u00a7d] \u00a7fThe plugin has been \u00a7dEnabled\u00a7f!");
            Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBosterItemSpawner\u00a7d] \u00a7fPlugin creator: \u00a7dBosternike");
            Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBosterItemSpawner\u00a7d] \u00a7fPlugin creator URL: \u00a7dvk.com/bosternike");
            Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBosterItemSpawner\u00a7d] \u00a7fPlugin version: \u00a7d" + getDescription().getVersion());
            Bukkit.getConsoleSender().sendMessage("\u00a76+\u00a7a---------------- \u00a7dBosterItemSpawner \u00a7a------------------\u00a76+");
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

    public void log(String s, LogType log) {
        Bukkit.getConsoleSender().sendMessage(log.getFormat() + log.getColor() + Utils.toColor(s));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new Events(), this);
        if(Version.getCurrentVersion().getVersionInteger() < 8) {
            getServer().getPluginManager().registerEvents(new OldPickupListener(), this);
        } else {
            getServer().getPluginManager().registerEvents(new NewPickupListener(), this);
        }
    }
}
