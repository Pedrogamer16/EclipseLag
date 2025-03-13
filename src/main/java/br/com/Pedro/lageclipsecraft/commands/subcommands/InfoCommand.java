package br.com.Pedro.lageclipsecraft.commands.subcommands;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.commands.EclipseLagCommand;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class InfoCommand implements EclipseLagCommand.SubCommand {

    private final LagEclipseCraft plugin;

    public InfoCommand(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Remove TPS
        for (World world : Bukkit.getWorlds()) {
            MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("world_info", "%world%", world.getName(), "%count%", String.valueOf(world.getEntities().size())));
        }
    }
}