package br.com.Pedro.lageclipsecraft.commands.subcommands;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.commands.EclipseLagCommand;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbyssCommand implements EclipseLagCommand.SubCommand {

    private final LagEclipseCraft plugin;

    public AbyssCommand(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            player.openInventory(plugin.getWorldCleanerManager().getAbyss());
        } else {
            MessageUtil.sendMessage(sender, "&cApenas jogadores podem abrir o abyss!");
        }
    }
}