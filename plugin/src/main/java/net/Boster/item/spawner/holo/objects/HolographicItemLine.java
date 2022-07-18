package net.Boster.item.spawner.holo.objects;

import lombok.Getter;
import net.Boster.item.spawner.holo.HoloLineType;
import net.Boster.item.spawner.holo.HoloPosition;
import net.Boster.item.spawner.holo.Hologram;
import net.Boster.item.spawner.holo.entity.NMSItemEntity;
import net.Boster.item.spawner.holo.line.HoloItemLine;
import net.Boster.item.spawner.nms.HologramsProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HolographicItemLine implements HoloItemLine {

    @Getter @NotNull private final Hologram hologram;
    @Getter @NotNull private HoloPosition position;
    @Getter @Nullable private ItemStack item;

    @Getter private boolean destroyed = false;

    @Getter @NotNull private NMSItemEntity entity;

    public HolographicItemLine(@NotNull Hologram holo, @NotNull HoloPosition pos, @Nullable ItemStack item) {
        this.hologram = holo;
        this.position = pos;
        this.item = item;
        this.entity = (NMSItemEntity) HologramsProvider.getProvider().createLineEntity(this);
        HologramsProvider.getProvider().show(this, hologram.getViewers());
    }

    @Override
    public void setItem(@Nullable ItemStack item) {
        this.item = item;
        entity.setItemStack(item);
        HologramsProvider.getProvider().show(this, hologram.getViewers());
    }

    @Override
    public @NotNull HoloLineType getType() {
        return HoloLineType.ITEM;
    }

    @Override
    public void move(@NotNull HoloPosition pos) {
        entity.destroy();
        position = pos;
        entity = (NMSItemEntity) HologramsProvider.getProvider().createLineEntity(this);
    }

    @Override
    public void spawn() {
        if(!destroyed) return;

        entity = (NMSItemEntity) HologramsProvider.getProvider().createLineEntity(this);
    }

    @Override
    public void destroy() {
        if(destroyed) return;

        destroyed = true;
        entity.destroy();
    }
}
