package br.com.Pedro.lageclipsecraft.modules.lagmonitor;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class LagMonitorManager {

    private final LagEclipseCraft plugin;

    public LagMonitorManager(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    public void start() {
        // Implementar monitoramento de lag se necessário
    }

    public void showMap(CommandSender sender) {
        // Remove TPS
        for (World world : Bukkit.getWorlds()) {
            MessageUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("world_info", "%world%", world.getName(), "%count%", String.valueOf(world.getEntities().size())));
        }
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        MessageUtil.sendMessage(sender, "&eMemória usada: " + usedMemory + " MB");
    }
}