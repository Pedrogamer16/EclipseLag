package br.com.Pedro.lageclipsecraft.commands.subcommands;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.commands.EclipseLagCommand;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.command.CommandSender;

public class ConfigCommand implements EclipseLagCommand.SubCommand {
    private final LagEclipseCraft plugin;

    public ConfigCommand(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lagfixer.command.config")) {
            MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("permission_denied", "%perm%", "lagfixer.command.config"));
            return;
        }

        if (args.length < 4) {
            MessageUtil.sendMessage(sender, "&cUso: /eclipselag config <módulo> <opção> <valor>");
            return;
        }

        String path = "modules." + args[1] + ".values." + args[2];
        try {
            Object value;
            if (args[3].matches("-?\\d+")) {
                value = Integer.parseInt(args[3]);
            } else if (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")) {
                value = Boolean.parseBoolean(args[3]);
            } else {
                value = args[3];
            }
            plugin.getConfigManager().getConfig().set(path, value);
            plugin.getConfigManager().reloadConfig();
            MessageUtil.sendMessage(sender, "&aConfiguração %path% atualizada para %value%!".replace("%path%", path).replace("%value%", args[3]));
        } catch (Exception e) {
            MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("config_error", "%error%", e.getMessage()));
        }
    }
}