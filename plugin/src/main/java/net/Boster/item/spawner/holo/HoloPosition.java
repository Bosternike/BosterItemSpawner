package net.Boster.item.spawner.holo;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface HoloPosition extends Cloneable {

    @NotNull World getWorld();

    int getBlockX();
    int getBlockY();
    int getBlockZ();

    double getX();
    double getY();
    double getZ();

    int getChunkX();
    int getChunkZ();

    boolean isInChunk(@NotNull Chunk chunk);

    @NotNull HoloPosition add(double x, double y, double z);
    @NotNull HoloPosition subtract(double x, double y, double z);

    @NotNull HoloPosition clone();

    @NotNull Location toLocation();
}
