package br.com.Pedro.lageclipsecraft.modules.redstonelimiter;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedstoneLimiterManager {

    private final LagEclipseCraft plugin;
    private final Map<Chunk, Integer> redstoneCounts = new HashMap<>();
    private final Set<Material> redstoneBlocks = Set.of(
            Material.REDSTONE_WIRE, Material.REDSTONE_TORCH, Material.REDSTONE_WALL_TORCH,
            Material.REPEATER, Material.COMPARATOR, Material.PISTON, Material.STICKY_PISTON,
            Material.OBSERVER, Material.DISPENSER, Material.DROPPER, Material.HOPPER
    );

    public RedstoneLimiterManager(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    public void start() {
        // Inicialização
    }

    public void handleRedstoneEvent(BlockRedstoneEvent event) {
        if (!plugin.getConfigManager().getConfig().getBoolean("modules.RedstoneLimiter.enabled")) return;
        int maxTicks = plugin.getConfigManager().getConfig().getInt("modules.RedstoneLimiter.values.ticks_limit.redstone", 1000);
        if (event.getNewCurrent() > maxTicks) {
            event.setNewCurrent(0);
        }
    }

    public void handleBlockPlace(BlockPlaceEvent event) {
        if (!plugin.getConfigManager().getConfig().getBoolean("modules.RedstoneLimiter.enabled")) return;
        Block block = event.getBlock();
        if (!redstoneBlocks.contains(block.getType())) return;

        Chunk chunk = block.getChunk();
        int limit = plugin.getConfigManager().getConfig().getInt("modules.RedstoneLimiter.values.per_chunk_limit", 50);
        int count = redstoneCounts.getOrDefault(chunk, 0);

        if (count >= limit) {
            event.setCancelled(true);
            MessageUtil.sendMessage(event.getPlayer(), plugin.getLanguageManager().getMessage("redstone_limit_exceeded"));
        } else {
            redstoneCounts.put(chunk, count + 1);
        }
    }

    public void removeRedstoneCount(Block block) {
        if (redstoneBlocks.contains(block.getType())) {
            Chunk chunk = block.getChunk();
            redstoneCounts.compute(chunk, (k, v) -> v == null || v <= 1 ? null : v - 1);
        }
    }
}