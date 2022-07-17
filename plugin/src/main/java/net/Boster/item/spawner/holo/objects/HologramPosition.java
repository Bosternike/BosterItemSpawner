package net.Boster.item.spawner.holo.objects;

import lombok.Getter;
import net.Boster.item.spawner.holo.HoloPosition;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HologramPosition implements HoloPosition {

    @Getter @NotNull private final World world;

    @Getter private double x;
    @Getter private double y;
    @Getter private double z;

    @Getter private final int chunkX;
    @Getter private final int chunkZ;

    public HologramPosition(@NotNull Location loc) {
        this.world = Objects.requireNonNull(loc.getWorld());

        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();

        this.chunkX = loc.getChunk().getX();
        this.chunkZ = loc.getChunk().getZ();
    }

    @Override
    public int getBlockX() {
        return toBlock(x);
    }

    @Override
    public int getBlockY() {
        return toBlock(y);
    }

    @Override
    public int getBlockZ() {
        return toBlock(z);
    }

    @Override
    public boolean isInChunk(@NotNull Chunk c) {
        int xp = c.getX() * 16;
        int zp = c.getZ() * 16;
        int x = chunkX;
        int z = chunkZ;

        return (xp <= x) && (xp + 15 >= x) && (zp <= z) && (zp + 15 >= z);
    }

    @Override
    public @NotNull HoloPosition add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    public @NotNull HoloPosition subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    @Override
    public @NotNull HoloPosition clone() {
        try {
            return (HoloPosition) super.clone();
        } catch (CloneNotSupportedException e) {
            /*This may never happen*/
            return this;
        }
    }

    @Override
    public @NotNull Location toLocation() {
        return new Location(world, x, y, z);
    }

    private int toBlock(double d) {
        int floor = (int) d;
        return floor == d ? floor : floor - (int) (Double.doubleToRawLongBits(d) >>> 63);
    }
}
