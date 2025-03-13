package br.com.Pedro.lageclipsecraft.listeners;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntityListener implements Listener {

    private final LagEclipseCraft plugin;

    public EntityListener(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        plugin.getEntityLimiterManager().handleEntitySpawn(event);
        plugin.getCustomVehiclesManager().handleEntitySpawn(event);
    }
}