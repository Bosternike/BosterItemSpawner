package net.Boster.item.spawner.spawner;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.holo.HoloLineType;
import net.Boster.item.spawner.manager.SpawnersManager;
import net.Boster.item.spawner.spawner.counter.AutomaticCounter;
import net.Boster.item.spawner.spawner.counter.PremadeCounter;
import net.Boster.item.spawner.spawner.counter.SpawnerCounter;
import net.Boster.item.spawner.utils.LogType;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ItemSpawner extends AbstractSpawner {

    private static final HashMap<String, ItemSpawner> hash = new HashMap<>();

    public final String name;
    public final SpawnerFile file;
    public int spawnDelay;
    public int ticked = 0;
    public ItemStack item;

    public boolean spawnLimitEnabled;
    public int spawnLimitAmount;
    public boolean spawnLimitDisableCounter;

    public SpawnerCounter spawnerCounter;

    public final List<Item> droppedItems = new ArrayList<>();

    public ItemSpawner(SpawnerFile file, String name, Location loc, List<String> lines) {
        super(loc, lines);
        hash.put(name, this);
        this.name = name;
        this.file = file;
        if(file.getFile().getBoolean("CounterFormat.Automatic", true)) {
            spawnerCounter = new AutomaticCounter(this);
        } else {
            spawnerCounter = new PremadeCounter(this);
        }
        spawnerCounter.refresh();
    }

    @Override
    public void tick() {
        filterItems();

        boolean b1 = false;
        boolean b2 = false;
        if(spawnLimitEnabled && droppedItems.size() >= spawnLimitAmount) {
            b1 = true;
            if(spawnLimitDisableCounter) {
                b2 = true;
            }
        }

        if(ticked + 1 >= spawnDelay) {
            if(!b2) {
                ticked = 0;
                spawnerCounter.refresh();
            }
            if(!b1) {
                if(item != null) {
                    dropItem();
                }
            }
        } else {
            if(!b2) {
                ticked++;
                spawnerCounter.secondRun();
            }
        }

        updateLines();
    }

    public void dropItem() {
        if(SpawnersManager.getTaskManager().isAsync()) {
            Bukkit.getScheduler().runTask(BosterItemSpawner.getInstance(), () -> {
                droppedItems.add(location.getWorld().dropItemNaturally(location, item.clone()));
            });
        } else {
            droppedItems.add(location.getWorld().dropItemNaturally(location, item.clone()));
        }
    }

    public void updateLines() {
        for(int l = 0; l < getLines().size(); l++) {
            String s = lines.get(l);
            if(s.equalsIgnoreCase("%item%")) {
                showItem(l);
                continue;
            }

            s = Utils.toColor(s.replace("%delay%", Integer.toString(spawnDelay))
                    .replace("%spawn_in%", Integer.toString(spawnDelay - ticked))
                    .replace("%spawn_in_formatted%", spawnerCounter.getAsString()));
            insertLine(l, s);
        }
    }

    public void filterItems() {
        for(int i = 0; i < droppedItems.size();) {
            if(!droppedItems.get(i).isValid()) {
                droppedItems.remove(i);
            } else {
                i++;
            }
        }
    }

    public void stopTask() {
        super.stopTask();
    }

    public void showItem(int i) {
        ItemStack itemStack = item.clone();
        itemStack.setAmount(1);

        if(hologram.getLines().size() > i && hologram.getLines().get(i).getType() == HoloLineType.ITEM) return;

        hologram.getLines().insertItem(i, itemStack);
    }

    public void start() {
        if(hologram.isDestroyed()) {
            setupHologram();
        }

        run();
    }

    public void clearDrops() {
        for(Item i : droppedItems) {
            i.remove();
        }
    }

    public void log(String s, LogType type) {
        BosterItemSpawner.getInstance().log("&7(Spawner: " + type.getColor() + name + "&7): " + s, type);
    }

    public static ItemSpawner get(String name) {
        return hash.get(name);
    }

    public void removeHolograms() {
        super.remove();
    }

    public void remove() {
        stopTask();
        removeHolograms();
        clearDrops();
    }

    public void clear() {
        hash.remove(name);
    }

    public boolean isInChunk(@NotNull Chunk c) {
        return hologram.getPosition().isInChunk(c) || Utils.chunkEquals(c, location.getChunk());
    }

    public List<Item> dropsInChunk(@NotNull Chunk c) {
        return droppedItems.stream().filter(i -> Utils.chunkEquals(c, i.getLocation().getChunk())).collect(Collectors.toList());
    }

    public static Collection<ItemSpawner> spawners() {
        return hash.values();
    }
}
