package net.Boster.item.spawner.hologram;
import lombok.Getter;
import net.Boster.item.spawner.utils.ReflectionUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomHologram {

    @Getter @NotNull private final Location location;
    @Getter private final ArmorStand[] holograms;
    @Getter @NotNull private final List<Location> linesLocations = new ArrayList<>();

    public CustomHologram(@NotNull Location loc, int lines) {
        this.location = loc;
        this.holograms = new ArmorStand[lines];
        Location lc = loc.clone();
        linesLocations.add(lc.clone());
        for(int i = 1; i < lines; i++) {
            linesLocations.add(lc.subtract(0, 0.3, 0).clone());
        }
    }

    public void setLine(int i, String s) {
        if(i >= holograms.length) {
            return;
        }

        if(s == null) {
            if(holograms[i] != null) {
                holograms[i].remove();
                holograms[i] = null;
            }
        } else {
            if(holograms[i] == null) {
                holograms[i] = ReflectionUtils.getArmorStand(linesLocations.get(i), true, true);
                holograms[i].setGravity(false);
                holograms[i].setCanPickupItems(false);
                holograms[i].setCustomNameVisible(false);
                holograms[i].setMarker(true);
            }
            holograms[i].setCustomName(s);
            holograms[i].setCustomNameVisible(holograms[i].getCustomName() != null && !ChatColor.stripColor(holograms[i].getCustomName()).equals(""));
        }
    }

    public void remove() {
        for(ArmorStand s : holograms) {
            if(s != null) {
                s.remove();
            }
        }
    }
}
