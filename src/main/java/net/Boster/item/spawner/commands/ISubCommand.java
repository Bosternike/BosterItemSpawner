package net.Boster.item.spawner.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ISubCommand {

    @NotNull Plugin getPlugin();
    @NotNull String[] getArguments();
    boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args);

    default boolean hasPermission(@NotNull CommandSender sender) {
        return true;
    }

    default List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return null;
    }

    default @NotNull List<String> getHelp() {
        return ImmutableList.of();
    }
}
