package net.Boster.item.spawner.commands.subcommands;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.commands.SubCommand;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.spawner.ItemSpawner;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class TeleportCommand extends SubCommand {

    public TeleportCommand(BosterItemSpawner plugin) {
        super(plugin, "teleport", "tp");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!checkPermission(sender)) return false;

        if(args.length == 0) {
            sendUsage(sender);
            return false;
        }

        ItemSpawner is = ItemSpawner.get(args[0]);
        if(is == null) {
            sender.sendMessage(Utils.toColor(getMessageNullSpawner(args[0])));
            return false;
        }

        Player p;
        String s;
        if(args.length == 1) {
            if(isConsole(sender)) return false;

            p = (Player) sender;
            s = getMessage("success").replace("%name%", args[0]);
        } else {
            p = Bukkit.getPlayer(args[1]);

            if(p == null) {
                sender.sendMessage(Utils.toColor(getMessage("nullPlayer").replace("%name%", args[1])));
                return false;
            }

            s = getMessage("successOthers").replace("%player%", args[1]).replace("%name%", args[0]);
        }

        p.teleport(is.getLocation());
        sender.sendMessage(Utils.toColor(s));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if(args.length < 2) {
            return createDefaultTabComplete(SpawnerFile.getValues().stream().map(SpawnerFile::getName).collect(Collectors.toList()), args, 1);
        } else {
            return createDefaultTabComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args, 1);
        }
    }
}
