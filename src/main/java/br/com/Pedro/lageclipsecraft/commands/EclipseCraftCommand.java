package br.com.Pedro.lageclipsecraft.commands;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EclipseCraftCommand implements CommandExecutor {

    private final LagEclipseCraft plugin;

    public EclipseCraftCommand(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lagfixer.command")) {
            MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("no_permission"));
            return true;
        }

        if (args.length == 0) {
            MessageUtil.sendMessage(sender, "&eComandos do EclipseCraft:");
            MessageUtil.sendMessage(sender, "&e/eclipsecraft version - Mostra a versão do plugin");
            return true;
        }

        if (args[0].equalsIgnoreCase("version")) {
            MessageUtil.sendMessage(sender, "&eLagEclipseCraft v" + plugin.getDescription().getVersion());
        } else {
            MessageUtil.sendMessage(sender, "&cComando inválido! Use /eclipsecraft <version>");
        }
        return true;
    }
}