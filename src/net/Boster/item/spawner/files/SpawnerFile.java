package net.Boster.item.spawner.files;

import net.Boster.item.spawner.hologram.ItemSpawner;
import net.Boster.item.spawner.manager.SpawnersManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;

public class SpawnerFile {
	
	private static final LinkedHashMap<String, SpawnerFile> hash = new LinkedHashMap<>();
	
	private FileConfiguration config;
	private final File file;
	private final String name;
	
	public SpawnerFile(File file) {
		String[] ss = file.getName().replace(".", ",").split(",");
		this.name = ss[ss.length - 2];
		hash.put(name, this);
		this.file = file;
	}
	
	public static SpawnerFile get(String file) {
		return hash.get(file);
	}
	
	public FileConfiguration getFile() {
		return config;
	}

	public void delete() {
		if(file != null && file.exists()) {
			file.delete();
		}
	}
	
	public void reloadFile() {
		config = YamlConfiguration.loadConfiguration(file);
	}

	public void save() {
		try {
			config.save(file);
		} catch (Exception ignored) {}
	}
	
	public void create() {
        if(file.exists()) {
        	config = YamlConfiguration.loadConfiguration(file);
        }
    }
	
	public String getName() {
		return name;
	}
	
	public void clear() {
		hash.remove(name);
	}
	
	public static Collection<SpawnerFile> getValues() {
		return hash.values();
	}
	
	public static void clearAll() {
		hash.clear();
	}

	public void tryLoad() {
		ItemSpawner i = ItemSpawner.get(name);
		if(i != null) {
			i.stopTask();
			i.clearDrops();
			i.remove();
		}

		SpawnersManager.loadSpawner(this);
	}
}
