package net.Boster.item.spawner.holo.line;

import net.Boster.item.spawner.holo.HoloLineType;
import net.Boster.item.spawner.holo.HoloPosition;
import net.Boster.item.spawner.holo.Hologram;
import net.Boster.item.spawner.holo.entity.NMSEntity;
import org.jetbrains.annotations.NotNull;

public interface HoloLine {

    @NotNull Hologram getHologram();
    @NotNull HoloLineType getType();
    @NotNull HoloPosition getPosition();
    @NotNull NMSEntity getEntity();
    default double getHeight() {
        return getType().getHeight();
    }

    void move(@NotNull HoloPosition position);

    void spawn();
    void destroy();
    boolean isDestroyed();
}
