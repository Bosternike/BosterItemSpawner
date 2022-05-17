package net.Boster.item.spawner.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.utils.colorutils.ColorUtils;
import net.Boster.item.spawner.utils.colorutils.NewColorUtils;
import net.Boster.item.spawner.utils.colorutils.OldColorUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class Utils {

    private static ColorUtils colorUtils;
    public static ItemStack SKULL;

    static {
        try {
            SKULL = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        } catch (Exception e) {
            SKULL = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        }

        try {
            ChatColor.class.getMethod("of", String.class);
            colorUtils = new NewColorUtils();
        } catch (NoSuchMethodException e) {
            colorUtils = new OldColorUtils();
        }
    }

    public static String toColor(String s) {
        if(s == null) return null;

        return colorUtils.toColor(s.replace("%prefix%", BosterItemSpawner.getInstance().getConfig().getString("Settings.Prefix", "null")));
    }

    public static String toReplaces(String s, String... replaces) {
        if(s == null) return null;

        if(replaces != null) {
            for(int i = 0; i < replaces.length; i++) {
                if(i + 1 < replaces.length) {
                    s = s.replace(replaces[i], replaces[i + 1]);
                }
                i++;
            }
        }

        return s;
    }

    public static ItemStack getCustomSkull(String value) {
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", value));

        ItemStack skull = SKULL.clone();
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static Location getLocation(String s) {
        if(s == null) return null;

        try {
            String[] ss = s.split(", ");
            World w = Bukkit.getWorld(ss[0]);
            double x = Double.parseDouble(ss[1]);
            double y = Double.parseDouble(ss[2]);
            double z = Double.parseDouble(ss[3]);
            if(ss.length >= 6) {
                float pitch = Float.parseFloat(ss[4]);
                float yaw = Float.parseFloat(ss[5]);
                return new Location(w, x, y, z, pitch, yaw);
            } else {
                return new Location(w, x, y, z);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String locationToString(Location loc, boolean withPitchAndYaw) {
        return loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() +
                (withPitchAndYaw ? ", " + loc.getYaw() + ", " + loc.getPitch() : "");
    }

    @NotNull
    public static String serializeItem(@NotNull ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to save item stacks.", e);
        }
    }

    @NotNull
    public static ItemStack deserializeItem(@NotNull String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack item = (ItemStack) dataInput.readObject();

            dataInput.close();
            return item;
        } catch (ClassNotFoundException | IOException e) {
            throw new IllegalArgumentException("Unable to decode class type.", e);
        }
    }

    public static boolean chunkEquals(@NotNull Chunk c1, @NotNull Chunk c2) {
        return c1.getWorld() == c2.getWorld() && c1.getX() == c2.getX() && c1.getZ() == c2.getZ();
    }
}
