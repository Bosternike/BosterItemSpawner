package net.Boster.item.spawner.hologram;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.hologram.counter.AutomaticCounter;
import net.Boster.item.spawner.hologram.counter.PremadeCounter;
import net.Boster.item.spawner.hologram.counter.SpawnerCounter;
import net.Boster.item.spawner.utils.LogType;
import net.Boster.item.spawner.utils.ReflectionUtils;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ItemSpawner extends AbstractHologram {

    public static final String NO_PICKUP = "§bBosterItemSpawner §eitem, that can't be picked up!";

    private static final HashMap<String, ItemSpawner> hash = new HashMap<>();

    public final String name;
    public final SpawnerFile file;
    public int spawnDelay;
    public int ticked = 0;
    public int delayTicksAmount;
    public ItemStack item;

    public boolean spawnLimitEnabled;
    public int spawnLimitAmount;
    public boolean spawnLimitDisableCounter;

    public boolean showItem;
    public int showItemI;
    public double showItemT;
    public Item shownItem;

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
                    droppedItems.add(location.getWorld().dropItemNaturally(location, item.clone()));
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

    public void updateLines() {
        for(int i = 0; i < getLines().size(); i++) {
            String s = getLines().get(i).replace("%delay%", Integer.toString(spawnDelay))
                    .replace("%spawn_in%", Integer.toString(spawnDelay - ticked))
                    .replace("%spawn_in_formatted%", spawnerCounter.getAsString());
            getHologram().setLine(i, Utils.toColor(s));
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

    public void loadShownItem() {
        showItem = file.getFile().getBoolean("ShowItem.Enabled", true);
        if(showItem) {
            if(file.getFile().getBoolean("ShowItem.InTop", true)) {
                showItemI = 0;
                showItemT = 0.3;
            } else {
                showItemI = hologram.getLinesLocations().size() - 1;
                showItemT = -0.3;
            }
        }
    }

    public void showItem() {
        if(showItem) {
            ItemStack itemStack = item.clone();
            itemStack.setAmount(1);
            shownItem = ReflectionUtils.dropItem(hologram.getLinesLocations().get(showItemI).clone().add(0, showItemT, 0), itemStack, true);
            shownItem.setCustomNameVisible(false);
            shownItem.setCustomName(NO_PICKUP);
        }
    }

    public void start() {
        showItem();
        run(delayTicksAmount, delayTicksAmount);
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
        if(shownItem != null) {
            shownItem.remove();
        }
        super.remove();
    }

    public void remove() {
        removeHolograms();
        hash.remove(name);
    }

    public static List<ItemSpawner> getHolograms(Location loc) {
        List<ItemSpawner> list = new ArrayList<>();

        if(loc == null) return list;

        for(ItemSpawner h : holograms()) {
            if(h.shownItem != null && h.shownItem.getLocation().equals(loc)) {
                list.add(h);
                continue;
            }
            for(Location l : h.getHologram().getLinesLocations()) {
                if(loc.equals(l)) {
                    list.add(h);
                }
            }
        }

        return list;
    }

    public static ItemSpawner getHologram(Entity e) {
        if(e == null) return null;

        for(ItemSpawner h : holograms()) {
            if(h.shownItem != null && h.shownItem == e) {
                return h;
            }
            for(int i = 0; i < h.getHologram().getHolograms().length; i++) {
                if(h.getHologram().getHolograms()[i] == e) {
                    return h;
                }
            }
        }

        return null;
    }

    public static Collection<ItemSpawner> holograms() {
        return hash.values();
    }
}
