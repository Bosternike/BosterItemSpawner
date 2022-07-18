package net.Boster.item.spawner.spawner;

import lombok.Getter;
import lombok.Setter;
import net.Boster.item.spawner.holo.HoloLineType;
import net.Boster.item.spawner.holo.Hologram;
import net.Boster.item.spawner.holo.line.HoloLine;
import net.Boster.item.spawner.holo.line.HoloTextLine;
import net.Boster.item.spawner.manager.SpawnersManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractSpawner {

    @Getter @NotNull protected final Location location;
    @Getter @NotNull protected Hologram hologram;
    @Getter @NotNull protected final List<String> lines;

    @Getter @Setter protected int delayTicksAmount = 1;
    @Getter @Setter protected int ticksCounted = 0;

    public AbstractSpawner(@NotNull Location loc, @NotNull List<String> lines) {
        this.location = loc;
        this.lines = lines;
        setupHologram();
    }

    protected void setupHologram() {
        hologram = new Hologram(location);
        hologram.showForAll();
    }

    public void run() {
        if(isRunning()) return;

        SpawnersManager.getTaskManager().addSpawner(this);
    }

    public void stopTask() {
        SpawnersManager.getTaskManager().removeSpawner(this);
    }

    public void insertLine(int i, String s) {
        if(hologram.getLines().size() > i) {
            HoloLine line = hologram.getLines().get(i);
            if(line.getType() == HoloLineType.TEXT) {
                setLine((HoloTextLine) line, s);
            }
        } else {
            insertLine1(i, s);
        }
    }

    private void insertLine1(int i, String s) {
        String c = ChatColor.stripColor(s);
        hologram.getLines().insertText(i, c == null || c.isEmpty() ? null : s);
    }

    protected void setLine(HoloTextLine line, String s) {
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
        if(!hologram.isDestroyed()) {
            hologram.destroy();
        }
    }
}
