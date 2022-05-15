package net.Boster.item.spawner.commands.subcommands;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.commands.Commands;
import net.Boster.item.spawner.commands.SubCommand;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends SubCommand {

    public HelpCommand(BosterItemSpawner plugin) {
        super(plugin, "help");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!checkPermission(sender)) return false;

        List<String> commands = new ArrayList<>();

        for(String s : getMessages("sequence")) {
            SubCommand cmd = Commands.getSubCommand(s);
            if(cmd != null) {
                if(cmd.hasPermission(sender)) {
                    commands.addAll(cmd.getHelp());
                }
            }
        }

        if(commands.isEmpty()) {
            sender.sendMessage(Utils.toColor(getMessage("noHelpCommands")));
            return false;
        }

        for(String s : getMessages("startWith")) {
            sender.sendMessage(Utils.toColor(s));
        }
        for(String s : commands) {
            sender.sendMessage(Utils.toColor(s));
        }
        for(String s : getMessages("endWith")) {
            sender.sendMessage(Utils.toColor(s));
        }
        return true;
    }
}
