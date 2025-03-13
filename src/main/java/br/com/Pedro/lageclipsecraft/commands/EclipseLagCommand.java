package br.com.Pedro.lageclipsecraft.commands;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.commands.subcommands.*;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class EclipseLagCommand implements CommandExecutor, TabCompleter {

    private final LagEclipseCraft plugin;
    private final Map<String, SubCommand> subCommands;

    public EclipseLagCommand(LagEclipseCraft plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();
        registerSubCommands();
    }

    private void registerSubCommands() {
        subCommands.put("clear", new ClearCommand(plugin));
        subCommands.put("reload", new ReloadCommand(plugin));
        subCommands.put("info", new InfoCommand(plugin));
        subCommands.put("map", new MapCommand(plugin));
        subCommands.put("abyss", new AbyssCommand(plugin));
        subCommands.put("profile", new ProfileCommand(plugin));
        subCommands.put("config", new ConfigCommand(plugin));
        subCommands.put("stats", new StatsCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("eclipselag.use")) {
            MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("no_permission"));
            return true;
        }

        if (args.length == 0) {
            MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("invalid_command"));
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            subCommand.execute(sender, args);
            if (plugin.getSettings().debugMode) {
                plugin.getLogger().log(Level.FINE, "Subcomando '{0}' executado por {1}", new Object[]{args[0], sender.getName()});
            }
        } else {
            MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("invalid_command"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("eclipselag.use")) {
            return null;
        }

        if (args.length == 1) {
            return Arrays.asList("clear", "reload", "info", "map", "abyss", "profile", "config", "stats");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("clear")) {
            return Arrays.asList("items", "creatures", "projectiles");
        }
        return null;
    }

    public interface SubCommand {
        void execute(CommandSender sender, String[] args);
    }
}