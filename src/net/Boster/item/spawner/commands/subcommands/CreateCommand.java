package net.Boster.item.spawner.commands.subcommands;


import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.manager.SpawnersManager;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreateCommand extends SubCommand {

    public CreateCommand(BosterItemSpawner plugin) {
        super(plugin, "create");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!checkPermission(sender)) return false;

        if(args.length == 0) {
            sendUsage(sender);
            return false;
        }

        if(SpawnerFile.get(args[0]) != null) {
            sender.sendMessage(Utils.toColor(getMessageNotNullSpawner(args[0])));
            return false;
        }

        if(sender instanceof Player p) {
            SpawnersManager.createSpawner(args[0], p.getLocation().add(0, 1, 0), p.getName());
        } else {
            SpawnersManager.createSpawner(args[0], null, sender.getName());
        }
        sender.sendMessage(Utils.toColor(getMessage("success").replace("%name%", args[0])));
        return true;
    }
}
