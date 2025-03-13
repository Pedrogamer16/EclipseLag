package br.com.Pedro.lageclipsecraft.commands.subcommands;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.commands.EclipseLagCommand;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class ProfileCommand implements EclipseLagCommand.SubCommand {

    private final LagEclipseCraft plugin;

    public ProfileCommand(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        long totalEntities = Bukkit.getWorlds().stream().mapToLong(world -> world.getEntities().size()).sum(); // Substitui World::getEntityCount
        long loadedChunks = Bukkit.getWorlds().stream().mapToLong(w -> w.getLoadedChunks().length).sum();
        MessageUtil.sendMessage(sender, "&eEntidades totais: " + totalEntities);
        MessageUtil.sendMessage(sender, "&eChunks carregados: " + loadedChunks);
    }
}