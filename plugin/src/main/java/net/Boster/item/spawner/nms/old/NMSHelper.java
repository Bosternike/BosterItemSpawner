package net.Boster.item.spawner.nms.old;

import net.Boster.item.spawner.utils.Version;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Ref;

public class NMSHelper {

    private static Field playerConnection;
    private static Method sendPacket;

    public static void load() {
        try {
            playerConnection = getNMSClass("EntityPlayer").getField("playerConnection");
            sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static @NotNull Class<?> getCBClass(String clazz) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + Version.getCurrentVersion().name() + "." + clazz);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static @NotNull Class<?> getNMSClass(String clazz) {
        try {
            return Class.forName("net.minecraft.server." + Version.getCurrentVersion().name() + "." + clazz);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static @NotNull Object getHandle(@NotNull Object o) {
        try {
            return o.getClass().getMethod("getHandle").invoke(o);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static void sendPacket(@NotNull Player p, @NotNull Object... packets) {
        sendPacket(getHandle(p), packets);
    }

    public static void sendPacket(@NotNull Object nmsPlayer, @NotNull Object... packets) {
        try {
            Object p = playerConnection.get(nmsPlayer);
            for(Object packet : packets) {
                sendPacket.invoke(p, packet);
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    public static @NotNull Method getMethod(@NotNull Class<?> clazz, @NotNull String method, @NotNull Class<?>... parameters) {
        try {
            return clazz.getMethod(method, parameters);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull Constructor<?> getConstructor(@NotNull Class<?> clazz, @NotNull Class<?>... parameters) {
        try {
            return clazz.getConstructor(parameters);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
