package br.com.Pedro.lageclipsecraft.commands.subcommands;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.commands.EclipseLagCommand;
import org.bukkit.command.CommandSender;

public class MapCommand implements EclipseLagCommand.SubCommand {

    private final LagEclipseCraft plugin;

    public MapCommand(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.getLagMonitorManager().showMap(sender);
    }
}