package net.Boster.item.spawner.hologram;

import com.gmail.filoghost.holographicdisplays.object.line.CraftTextLine;
import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.hologram.counter.AutomaticCounter;
import net.Boster.item.spawner.hologram.counter.PremadeCounter;
import net.Boster.item.spawner.hologram.counter.SpawnerCounter;
import net.Boster.item.spawner.utils.LogType;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ItemSpawner extends AbstractHologram {

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
    public boolean showItemT;
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
        int t = showItemT ? 1 : 0;
        for(int l = 0; l < getLines().size(); l++) {
            int i = l + t;
            String s = Utils.toColor(lines.get(l).replace("%delay%", Integer.toString(spawnDelay))
                    .replace("%spawn_in%", Integer.toString(spawnDelay - ticked))
                    .replace("%spawn_in_formatted%", spawnerCounter.getAsString()));
            if(hologram.size() > i) {
                setLine((CraftTextLine) hologram.getLine(i), s);
            } else {
                String c = ChatColor.stripColor(s);
                if(c == null || c.isEmpty()) {
                    hologram.insertTextLine(i, null);
                } else {
                    hologram.insertTextLine(i, s);
                }
            }
        }
    }

    private void setLine(CraftTextLine line, String s) {
        if(line.getText() == null && s == null) return;

        String c = ChatColor.stripColor(s);
        if((line.getText() == null || line.getText().isEmpty()) && (c == null || c.isEmpty())) return;

        line.setText(c != null && !c.isEmpty() ? s : null);
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
            showItemT = file.getFile().getBoolean("ShowItem.InTop", true);
        }
    }

    public void stopTask() {
        super.stopTask();
    }

    public void showItem() {
        if(showItem) {
            ItemStack itemStack = item.clone();
            itemStack.setAmount(1);
            if(showItemT) {
                hologram.insertItemLine(0, itemStack);
            } else {
                hologram.appendItemLine(itemStack);
            }
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

    public static Collection<ItemSpawner> holograms() {
        return hash.values();
    }
}
