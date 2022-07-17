package net.Boster.item.spawner.holo;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.Boster.item.spawner.holo.line.HoloLine;
import net.Boster.item.spawner.holo.objects.HologramPosition;
import net.Boster.item.spawner.nms.HologramsProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Hologram {

    @Getter @NotNull private HoloPosition position;
    @Getter @NotNull private final HologramLines lines;
    @Getter @NotNull private final List<Player> viewers = new LinkedList<>();

    @Getter private boolean deleted = false;

    public Hologram(@NotNull Location loc) {
        this(new HologramPosition(loc));
    }

    public Hologram(@NotNull HoloPosition position) {
        this.position = position;
        this.lines = new HologramLines(this);
    }

    public void move(@NotNull Location loc) {
        move(new HologramPosition(loc));
    }

    public void move(@NotNull HoloPosition pos) {
        position = pos;
        lines.synchronizePositions();
    }

    public @NotNull HoloPosition getNextLineLocation() {
        if(lines.size() == 0) {
            return position.clone();
        } else {
            HoloLine line = lines.get(lines.size() - 1);
            return line.getPosition().clone().subtract(0, line.getHeight(), 0);
        }
    }

    public void delete() {
        deleted = true;

        for(HoloLine line : lines) {
            if(!line.isDeleted()) {
                line.delete();
            }
        }
    }

    public void showForAll() {
        show(Bukkit.getOnlinePlayers());
    }

    public void show(@NotNull Player p) {
        show(ImmutableList.of(p));
    }

    public void show(@NotNull Collection<? extends Player> players) {
        for(HoloLine line : lines) {
            HologramsProvider.getProvider().show(line, players);
        }

        addViewers(players);
    }

    public void addViewers(@NotNull Collection<? extends Player> players) {
        players.stream().filter(p -> !viewers.contains(p)).forEach(viewers::add);
    }
}
