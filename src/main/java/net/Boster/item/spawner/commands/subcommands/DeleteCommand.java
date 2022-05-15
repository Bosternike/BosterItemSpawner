package net.Boster.item.spawner.commands.subcommands;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.commands.SubCommand;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.hologram.ItemSpawner;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DeleteCommand extends SubCommand {

    public DeleteCommand(BosterItemSpawner plugin) {
        super(plugin, "delete");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!checkPermission(sender)) return false;

        if(args.length == 0) {
            sendUsage(sender);
            return false;
        }

        SpawnerFile f = SpawnerFile.get(args[0]);
        if(f == null) {
            sender.sendMessage(Utils.toColor(getMessageNullSpawner(args[0])));
            return false;
        }

        f.delete();
        f.clear();
        ItemSpawner is = ItemSpawner.get(args[0]);
        if(is != null) {
            is.remove();
        }
        sender.sendMessage(Utils.toColor(getMessage("success").replace("%name%", args[0])));
        return true;
    }
}
