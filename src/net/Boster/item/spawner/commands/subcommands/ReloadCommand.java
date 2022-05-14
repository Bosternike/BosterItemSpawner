package net.Boster.item.spawner.commands.subcommands;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(BosterItemSpawner plugin) {
        super(plugin, "reload");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!checkPermission(sender)) return false;

        plugin.reload();
        sender.sendMessage(Utils.toColor(getMessage("success")));
        return true;
    }
}
