package net.Boster.item.spawner.holo.objects;

import lombok.Getter;
import net.Boster.item.spawner.holo.HoloLineType;
import net.Boster.item.spawner.holo.HoloPosition;
import net.Boster.item.spawner.holo.Hologram;
import net.Boster.item.spawner.holo.entity.NMSTextEntity;
import net.Boster.item.spawner.holo.line.HoloTextLine;
import net.Boster.item.spawner.nms.HologramsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HolographicTextLine implements HoloTextLine {

    @Getter @NotNull private final Hologram hologram;
    @Getter @NotNull private HoloPosition position;
    @Getter @Nullable private String text;

    @Getter @NotNull private NMSTextEntity entity;

    @Getter private boolean destroyed = false;

    public HolographicTextLine(@NotNull Hologram holo, @NotNull HoloPosition pos, @Nullable String text) {
        this.hologram = holo;
        this.position = pos;
        this.text = text;
        this.entity = (NMSTextEntity) HologramsProvider.getProvider().createLineEntity(this);
        HologramsProvider.getProvider().show(this, hologram.getViewers());
    }

    @Override
    public @NotNull HoloLineType getType() {
        return HoloLineType.TEXT;
    }

    @Override
    public void move(@NotNull HoloPosition pos) {
        entity.destroy();
        position = pos;
        entity = (NMSTextEntity) HologramsProvider.getProvider().createLineEntity(this);
    }

    @Override
    public void spawn() {
        if(!destroyed) return;

        entity = (NMSTextEntity) HologramsProvider.getProvider().createLineEntity(this);
    }

    @Override
    public void destroy() {
        if(destroyed) return;

        destroyed = true;
        entity.destroy();
    }

    @Override
    public void setText(@Nullable String text) {
        this.text = text;
        entity.setText(text);
        HologramsProvider.getProvider().show(this, hologram.getViewers());
    }
}
