package net.Boster.item.spawner.commands;

import net.Boster.item.spawner.BosterItemSpawner;
import net.Boster.item.spawner.commands.subcommands.*;
import net.Boster.item.spawner.files.SpawnerFile;
import net.Boster.item.spawner.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Commands implements CommandExecutor, TabCompleter {

    private final BosterItemSpawner plugin;

    public static final List<SubCommand> subCommands = new ArrayList<>();

    public Commands(BosterItemSpawner plugin) {
        this.plugin = plugin;
        subCommands.add(new HelpCommand(plugin));
        subCommands.add(new ReloadCommand(plugin));
        subCommands.add(new CreateCommand(plugin));
        subCommands.add(new EditCommand(plugin));
        subCommands.add(new InfoCommand(plugin));
        subCommands.add(new DeleteCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("boster.itemspawner.command.use")) {
            sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.noPermission")));
            return false;
        }

        if(args.length == 0) {
            sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.nullArgs")));
            return false;
        }

        for(SubCommand cmd : subCommands) {
            for(String a : cmd.getArguments()) {
                if(a.equalsIgnoreCase(args[0])) {
                    String[] arguments = new String[args.length - 1];
                    System.arraycopy(args, 1, arguments, 0, args.length - 1);
                    return cmd.onCommand(sender, arguments);
                }
            }
        }

        sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.invalidArgument").replace("%syntax%", args[0])));
        return false;
    }

    public static SubCommand getSubCommand(String argument) {
        for(SubCommand cmd : subCommands) {
            for(String a : cmd.getArguments()) {
                if(a.equalsIgnoreCase(argument)) {
                    return cmd;
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            subCommands.forEach(l -> list.addAll(Arrays.asList(l.getArguments())));
            return SubCommand.createDefaultTabComplete(list, args, 0);
        } else {
            for(SubCommand c : subCommands) {
                if(c.hasPermission(sender)) {
                    for(String i : c.getArguments()) {
                        if(i.equalsIgnoreCase(args[0])) {
                            String[] arguments = new String[args.length - 1];
                            System.arraycopy(args, 1, arguments, 0, args.length - 1);
                            return c.onTabComplete(sender, arguments);
                        }
                    }
                }
            }
        }

        return SubCommand.createDefaultTabComplete(SpawnerFile.getValues().stream().map(SpawnerFile::getName).collect(Collectors.toList()), args, args.length - 1);
    }
}
