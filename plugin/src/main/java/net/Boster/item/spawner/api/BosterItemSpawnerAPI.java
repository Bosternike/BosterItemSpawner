package net.Boster.item.spawner.api;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.commands.Commands;
import net.Boster.item.spawner.commands.ISubCommand;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.manager.SpawnersManager;
import net.Boster.item.spawner.spawner.ItemSpawner;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BosterItemSpawnerAPI {

    public static void reload() {
        BosterItemSpawner.getInstance().reload();
    }

    public static @Nullable SpawnerFile getSpawnerFile(@NotNull String name) {
        return SpawnerFile.get(name);
    }

    public static @Nullable ItemSpawner getItemSpawner(@NotNull SpawnerFile f) {
        return getItemSpawner(f.getName());
    }

    public static @Nullable ItemSpawner getItemSpawner(@NotNull String name) {
        return ItemSpawner.get(name);
    }

    /**
     * @return true if spawner was replaced and false if spawner was created.
     */
    public static boolean createSpawner(@NotNull String name, @Nullable Location loc, @Nullable String source) {
        boolean b = SpawnerFile.get(name) != null;

        SpawnersManager.createSpawner(name, loc, source);

        return b;
    }

    /**
     * @return false if spawner file wasn't found.
     */
    public static boolean deleteSpawner(@NotNull String name) {
        SpawnerFile f = SpawnerFile.get(name);
        if(f == null) {
            return false;
        }

        f.delete();
        f.clear();
        ItemSpawner is = ItemSpawner.get(name);
        if(is != null) {
            is.remove();
        }

        return true;
    }

    public static void setSpawnerLocation(@NotNull SpawnerFile f, @Nullable Location loc) {
        f.getFile().set("location", loc != null ? Utils.locationToString(loc, true) : null);
        f.save();
        f.tryLoad();
    }

    public static void setSpawnerItemStack(@NotNull SpawnerFile f, @Nullable ItemStack item) {
        f.getFile().set("item", item != null ? Utils.serializeItem(item) : null);
        f.save();
        f.tryLoad();
    }

    public static @Nullable Location getSpawnerLocation(@NotNull SpawnerFile f) {
        return Utils.getLocation(f.getFile().getString("location"));
    }

    public static @Nullable ItemStack getSpawnerItemStack(@NotNull SpawnerFile f) {
        String i = f.getFile().getString("item");
        if(i == null) return null;

        return Utils.deserializeItem(i);
    }

    public static boolean loadSpawner(@NotNull SpawnerFile f) {
        return SpawnersManager.loadSpawner(f);
    }

    public static void addSubCommand(@NotNull ISubCommand command) {
        Commands.subCommands.add(command);
    }

    public static void removeSubCommand(@NotNull ISubCommand command) {
        Commands.subCommands.remove(command);
    }

    public static boolean isDroppedItem(@NotNull Item item) {
        for(ItemSpawner s : ItemSpawner.spawners()) {
            if(s.droppedItems.contains(item)) return true;
        }

        return false;
    }
}
