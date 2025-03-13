package br.com.Pedro.lageclipsecraft.modules.customvehicles;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.HashMap;
import java.util.Map;

public class CustomVehiclesManager {

    private final LagEclipseCraft plugin;
    private final Map<Chunk, Integer> minecartCounts = new HashMap<>();

    public CustomVehiclesManager(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    public void start() {
        // Inicialização
    }

    public void handleEntitySpawn(EntitySpawnEvent event) {
        if (!plugin.getConfigManager().getConfig().getBoolean("modules.CustomVehicles.enabled")) return;
        Entity entity = event.getEntity();
        if (entity.getType().name().contains("MINECART")) {
            Chunk chunk = entity.getLocation().getChunk();
            int limit = plugin.getConfigManager().getConfig().getInt("modules.CustomVehicles.values.minecart_limit_per_chunk", 10);
            int count = minecartCounts.getOrDefault(chunk, 0);

            if (count >= limit) {
                event.setCancelled(true);
                if (!entity.getPassengers().isEmpty() && entity.getPassengers().get(0) instanceof Player player) { // Substitui getPassenger
                    MessageUtil.sendMessage(player, plugin.getLanguageManager().getMessage("minecart_limit_exceeded"));
                }
            } else {
                minecartCounts.put(chunk, count + 1);
            }
        }
    }

    public void removeMinecartCount(Entity entity) {
        if (entity.getType().name().contains("MINECART")) {
            Chunk chunk = entity.getLocation().getChunk();
            minecartCounts.compute(chunk, (k, v) -> v == null || v <= 1 ? null : v - 1);
        }
    }
}