package net.Boster.item.spawner.spawner;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import com.gmail.filoghost.holographicdisplays.object.line.CraftTextLine;
import lombok.Getter;
import lombok.Setter;
import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.manager.SpawnersManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractSpawner {

    @Getter @NotNull protected final Location location;
    @Getter @NotNull protected final CraftHologram hologram;
    @Getter @NotNull protected final List<String> lines;

    @Getter @Setter protected int delayTicksAmount = 1;
    @Getter @Setter protected int ticksCounted = 0;

    public AbstractSpawner(Location loc, @NotNull List<String> lines) {
        this.location = loc;
        this.lines = lines;
        this.hologram = (CraftHologram) HologramsAPI.createHologram(BosterItemSpawner.getInstance(), loc.clone());
    }

    public void run() {
        if(isRunning()) return;

        SpawnersManager.getTaskManager().addSpawner(this);
    }

    public void stopTask() {
        SpawnersManager.getTaskManager().removeSpawner(this);
    }

    public void insertLine(int i, String s) {
        if(hologram.size() > i) {
            setLine((CraftTextLine) hologram.getLine(i), s);
        } else {
            if(SpawnersManager.getTaskManager().isAsync()) {
                Bukkit.getScheduler().runTask(BosterItemSpawner.getInstance(), () -> {
                    insertLine1(i, s);
                });
            } else {
                insertLine1(i, s);
            }
        }
    }

    private void insertLine1(int i, String s) {
        String c = ChatColor.stripColor(s);
        if(c == null || c.isEmpty()) {
            hologram.insertTextLine(i, null);
        } else {
            hologram.insertTextLine(i, s);
        }
    }

    protected void setLine(CraftTextLine line, String s) {
        if(line.getText() == null && s == null) return;

        String c = ChatColor.stripColor(s);
        if((line.getText() == null || line.getText().isEmpty()) && (c == null || c.isEmpty())) return;

        line.setText(c != null && !c.isEmpty() ? s : null);
    }

    public boolean isRunning() {
        return SpawnersManager.getTaskManager().hasSpawner(this);
    }

    public abstract void tick();

    public void remove() {
        hologram.delete();
    }
}
