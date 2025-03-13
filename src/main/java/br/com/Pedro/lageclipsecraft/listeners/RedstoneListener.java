package br.com.Pedro.lageclipsecraft.listeners;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneListener implements Listener {

    private final LagEclipseCraft plugin;

    public RedstoneListener(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRedstoneEvent(BlockRedstoneEvent event) {
        plugin.getRedstoneLimiterManager().handleRedstoneEvent(event);
    }
}