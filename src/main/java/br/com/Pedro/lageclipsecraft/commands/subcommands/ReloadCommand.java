package br.com.Pedro.lageclipsecraft.commands.subcommands;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.commands.EclipseLagCommand;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements EclipseLagCommand.SubCommand {

    private final LagEclipseCraft plugin;

    public ReloadCommand(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.getConfigManager().reloadConfig();
        plugin.getLanguageManager().reloadLanguage();
        MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("config_reloaded"));
    }
}