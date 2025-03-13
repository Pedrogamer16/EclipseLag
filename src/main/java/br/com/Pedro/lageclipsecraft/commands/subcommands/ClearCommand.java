package br.com.Pedro.lageclipsecraft.commands.subcommands;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.commands.EclipseLagCommand;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ClearCommand implements EclipseLagCommand.SubCommand {

    private final LagEclipseCraft plugin;

    public ClearCommand(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            List<String> allowedWorlds = plugin.getSettings().getConfig().getStringList("modules.WorldCleaner.values.allowed_worlds");
            switch (args[1].toLowerCase()) {
                case "items":
                    for (World world : plugin.getServer().getWorlds()) {
                        if (allowedWorlds.contains("*") || allowedWorlds.contains(world.getName())) {
                            plugin.getWorldCleanerManager().cleanItems(world);
                        }
                    }
                    MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("items_cleared", "%world%", "todos os mundos"));
                    break;
                case "creatures":
                    for (World world : plugin.getServer().getWorlds()) {
                        if (allowedWorlds.contains("*") || allowedWorlds.contains(world.getName())) {
                            plugin.getWorldCleanerManager().cleanCreatures(world);
                        }
                    }
                    MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("entities_cleared"));
                    break;
                case "projectiles":
                    for (World world : plugin.getServer().getWorlds()) {
                        if (allowedWorlds.contains("*") || allowedWorlds.contains(world.getName())) {
                            plugin.getWorldCleanerManager().cleanProjectiles(world);
                        }
                    }
                    MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("entities_cleared"));
                    break;
                default:
                    MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("invalid_command"));
            }
        } else {
            plugin.getEntityLimiterManager().checkEntityLimits();
            MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("entities_cleared"));
        }
    }
}