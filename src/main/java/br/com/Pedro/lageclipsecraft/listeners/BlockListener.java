package br.com.Pedro.lageclipsecraft.listeners;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    private final LagEclipseCraft plugin;

    public BlockListener(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        plugin.getRedstoneLimiterManager().handleBlockPlace(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        plugin.getRedstoneLimiterManager().removeRedstoneCount(event.getBlock());
    }
}