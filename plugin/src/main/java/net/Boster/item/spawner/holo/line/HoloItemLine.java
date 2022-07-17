package net.Boster.item.spawner.holo.line;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface HoloItemLine extends HoloLine {

    @Nullable ItemStack getItem();
    void setItem(@Nullable ItemStack item);
}
