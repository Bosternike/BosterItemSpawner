package net.Boster.item.spawner.files;

import net.Boster.item.spawner.BosterItemSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SupportFile {

    private static final HashMap<String, SupportFile> hash = new HashMap<>();

    private FileConfiguration configuration;
    private File file;
    private final String name;
    private String dir = "";

    public SupportFile(String name) {
        hash.put(name, this);
        this.name = name;
    }

    public static SupportFile get(String file) {
        return hash.get(file);
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void create() {
        file = new File(BosterItemSpawner.getInstance().getDataFolder(), dir + name + ".yml");
        if(!file.exists()) {
            BosterItemSpawner.getInstance().saveResource(dir + name + ".yml", false);
        }

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void createNewFile() {
        file = new File(BosterItemSpawner.getInstance().getDataFolder(),dir + name + ".yml");
        if(!file.exists()) {
            File fd = new File(BosterItemSpawner.getInstance().getDataFolder(), dir);
            if(!fd.exists()) {
                fd.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public String getName() {
        return name;
    }

    public void clear() {
        hash.remove(name);
    }

    public static void clearAll() {
        hash.clear();
    }

    public void setDirectory(String s) {
        dir = s;
    }

    public String getDirectory() {
        return dir;
    }
}
