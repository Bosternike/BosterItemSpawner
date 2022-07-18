package net.Boster.item.spawner.holo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.Boster.item.spawner.holo.line.HoloItemLine;
import net.Boster.item.spawner.holo.line.HoloLine;
import net.Boster.item.spawner.holo.line.HoloTextLine;
import net.Boster.item.spawner.holo.objects.HolographicItemLine;
import net.Boster.item.spawner.holo.objects.HolographicTextLine;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public class HologramLines implements Iterable<HoloLine>, Cloneable {

    @Getter @NotNull private final Hologram hologram;

    private final List<HoloLine> lines = new LinkedList<>();

    public int size() {
        return lines.size();
    }

    @NotNull
    @Override
    public Iterator<HoloLine> iterator() {
        return lines.iterator();
    }

    public @NotNull HoloTextLine insertText(int i, @Nullable String text) {
        HoloPosition pos;
        if(i < lines.size()) {
            HoloLine l = lines.get(i);
            l.destroy();

            if(l.getHeight() != HoloLineType.TEXT.getHeight() && i + 1 < lines.size()) {
                moveLines(HoloLineType.TEXT.getHeight(), i);
            }

            pos = l.getPosition();
        } else {
            pos = hologram.getNextLineLocation();
        }

        HoloTextLine line = new HolographicTextLine(hologram, pos, text);
        if(i >= lines.size()) {
            lines.add(i, line);
        } else {
            lines.set(i, line);
        }
        return line;
    }

    private void moveLines(double height, int i) {
        for(int t = i; t < lines.size(); t++) {
            HoloLine line = lines.get(i);
            if(t == i) {
                line.move(line.getPosition().clone().subtract(0, height, 0));
            } else {
                HoloLine l = lines.get(t - 1);
                line.move(l.getPosition().clone().subtract(0, l.getHeight(), 0));
            }
        }
    }

    public @NotNull HoloTextLine appendText(@Nullable String text) {
        HoloTextLine line = new HolographicTextLine(hologram, hologram.getNextLineLocation(), text);
        lines.add(line);
        return line;
    }

    public @NotNull HoloItemLine insertItem(int i, @Nullable ItemStack item) {
        HoloPosition pos;
        if(i < lines.size()) {
            HoloLine l = lines.get(i);
            l.destroy();

            if(l.getHeight() != HoloLineType.ITEM.getHeight() && i + 1 < lines.size()) {
                moveLines(HoloLineType.ITEM.getHeight(), i);
            }

            pos = l.getPosition();
        } else {
            pos = hologram.getNextLineLocation();
        }

        HoloItemLine line = new HolographicItemLine(hologram, pos, item);
        if(i >= lines.size()) {
            lines.add(i, line);
        } else {
            lines.set(i, line);
        }
        return line;
    }

    public @NotNull HoloItemLine appendItem(@Nullable ItemStack item) {
        HoloItemLine line = new HolographicItemLine(hologram, hologram.getNextLineLocation(), item);
        lines.add(line);
        return line;
    }

    public HoloLine get(int i) {
        return lines.get(i);
    }

    public void synchronizePositions() {
        for(int i = 0; i < lines.size(); i++) {
            HoloLine line = lines.get(i);
            if(i == 0) {
                line.move(hologram.getPosition().clone());
            } else {
                HoloLine l = lines.get(i - 1);
                line.move(l.getPosition().clone().subtract(0, l.getHeight(), 0));
            }
        }
    }

    public void remove(int i) {
        lines.remove(i);
    }

    public void remove(@NotNull HoloLine line) {
        lines.remove(line);
    }

    @Override
    public HologramLines clone() {
        try {
            return (HologramLines) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
