package net.Boster.item.spawner.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ReflectionUtils {

    public static final HashMap<String, Class<?>> classCache = new HashMap<>();
    public static final String version;
    public static final int versionInt;

    static {
        version = Version.getCurrentVersion().name();
        versionInt = Integer.parseInt(version.split("_")[1]);

        try {
            loadClassCache("EntityArmorStand", "net.minecraft.server." + version, "net.minecraft.world.entity.decoration");
            loadClassCache("EntityTypes", "net.minecraft.server." + version, "net.minecraft.world.entity");
            loadClassCache("World", "net.minecraft.server." + version, "net.minecraft.world.level");
            loadClassCache("Entity", "net.minecraft.server." + version, "net.minecraft.world.entity");
            loadClassCache("Item", "net.minecraft.server." + version, "net.minecraft.world.item");
            loadClassCache("EntityItem", "net.minecraft.server." + version, "net.minecraft.world.entity.item");

            classCache.put("CraftArmorStand", Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftArmorStand"));
            classCache.put("CraftWorld", Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld"));
            classCache.put("CraftItemStack", Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadClassCache(String name, String oldPackage, String newPackage) throws ClassNotFoundException {
        if(Version.getCurrentVersion().getVersionInteger() < 13) {
            classCache.put(name, Class.forName(oldPackage + "." + name));
        } else {
            classCache.put(name, Class.forName(newPackage + "." + name));
        }
    }

    public static ArmorStand getArmorStand(Location loc, boolean invisible, boolean noGravity) {
        try {
            Object w = classCache.get("CraftWorld").getMethod("getHandle").invoke(loc.getWorld());
            Object e = classCache.get("EntityArmorStand").getConstructor(classCache.get("World"), double.class, double.class, double.class).newInstance(w, loc.getX(), loc.getY(), loc.getZ());
            e.getClass().getMethod("setInvisible", boolean.class).invoke(e, invisible);
            e.getClass().getMethod("setNoGravity", boolean.class).invoke(e, noGravity);
            w.getClass().getMethod("addEntity", classCache.get("Entity"), CreatureSpawnEvent.SpawnReason.class).invoke(w, e, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return (ArmorStand) classCache.get("EntityArmorStand").getMethod("getBukkitEntity").invoke(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Item dropItem(Location loc, ItemStack item, boolean noGravity) {
        Object i = craftAsNMSCopy(item);

        try {
            Object w = classCache.get("CraftWorld").getMethod("getHandle").invoke(loc.getWorld());
            Object e = classCache.get("EntityItem").getConstructor(classCache.get("World"), double.class, double.class, double.class, i.getClass()).newInstance(w, loc.getX(), loc.getY(), loc.getZ(), i);
            if(Version.getCurrentVersion().getVersionInteger() > 9) {
                classCache.get("Entity").getMethod("setMot", double.class, double.class, double.class).invoke(e, 0, 0, 0);
            }
            e.getClass().getMethod("setNoGravity", boolean.class).invoke(e, noGravity);
            w.getClass().getMethod("addEntity", classCache.get("Entity"), CreatureSpawnEvent.SpawnReason.class).invoke(w, e, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return (Item) classCache.get("EntityItem").getMethod("getBukkitEntity").invoke(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Item dropItem(Location loc, ItemStack item) {
        return dropItem(loc, item, false);
    }

    public static Object craftAsNMSCopy(ItemStack item) {
        try {
            return classCache.get("CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArmorStand getArmorStand(Location loc) {
        return getArmorStand(loc, false, true);
    }
}
