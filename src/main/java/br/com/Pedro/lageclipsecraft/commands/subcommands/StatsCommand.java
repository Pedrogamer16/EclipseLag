package br.com.Pedro.lageclipsecraft.commands.subcommands;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.commands.EclipseLagCommand;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Map;
import java.util.stream.Collectors;

public class StatsCommand implements EclipseLagCommand.SubCommand {
    private final LagEclipseCraft plugin;

    public StatsCommand(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        long entities = Bukkit.getWorlds().stream().mapToLong(world -> world.getEntities().size()).sum();
        long chunks = Bukkit.getWorlds().stream().mapToLong(w -> w.getLoadedChunks().length).sum();
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;

        MessageUtil.sendMessage(sender, "&eEntidades: " + entities);
        MessageUtil.sendMessage(sender, "&eChunks carregados: " + chunks);
        // Remove TPS
        MessageUtil.sendMessage(sender, "&eMemória usada: " + usedMemory + " MB");
        Bukkit.getWorlds().forEach(world -> {
            Map<EntityType, Long> counts = world.getEntities().stream()
                    .collect(Collectors.groupingBy(Entity::getType, Collectors.counting()));
            counts.forEach((type, count) ->
                    MessageUtil.sendMessage(sender, "&e" + type + " em " + world.getName() + ": " + count));
        });
    }
}