package br.com.Pedro.lageclipsecraft.modules.entitylimiter;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.config.Settings;
import br.com.Pedro.lageclipsecraft.utils.LogUtil;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class EntityLimiterManager {

    private final LagEclipseCraft plugin;
    private final Settings settings;
    private final Map<Chunk, Map<EntityType, Integer>> entityLimitsPerChunk = new WeakHashMap<>();
    private final Map<Chunk, Map<EntityType, Integer>> entityTypeCounts = new WeakHashMap<>();
    private final Map<Chunk, Integer> entityCountsPerChunk = new ConcurrentHashMap<>();
    private final Map<String, Long> lastMessageTime = new ConcurrentHashMap<>();

    public EntityLimiterManager(LagEclipseCraft plugin, Settings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    public void start() {
        if (!settings.entityLimiterEnabled) return;
        loadEntityLimits();
        startMonitoring();
        startChunkCleanup();
    }

    private void loadEntityLimits() {
        Map<EntityType, Integer> limits = new HashMap<>();
        settings.getConfig().getConfigurationSection("modules.EntityLimiter.values.perworld")
                .getKeys(false).forEach(key -> {
                    try {
                        limits.put(EntityType.valueOf(key.toUpperCase()), settings.getConfig().getInt("modules.EntityLimiter.values.perworld." + key));
                    } catch (IllegalArgumentException e) {
                        LogUtil.warn(ChatColor.RED + "Tipo de entidade inválido na config: " + key);
                    }
                });
        Bukkit.getWorlds().forEach(world -> {
            for (Chunk chunk : world.getLoadedChunks()) {
                entityLimitsPerChunk.put(chunk, limits);
                entityCountsPerChunk.put(chunk, 0);
                entityTypeCounts.put(chunk, new HashMap<>());
            }
        });
    }

    private void startMonitoring() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkEntityLimits();
            }
        }.runTaskTimer(plugin, 0L, settings.getConfig().getInt("main.monitor_interval", 5) * 20L);
    }

    private void startChunkCleanup() {
        new BukkitRunnable() {
            @Override
            public void run() {
                entityCountsPerChunk.entrySet().removeIf(entry -> !entry.getKey().isLoaded());
                entityTypeCounts.entrySet().removeIf(entry -> !entry.getKey().isLoaded());
                if (settings.getConfig().getBoolean("main.optimize_memory", true)) {
                    System.gc();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20 * 60 * 10L);
    }

    public void checkEntityLimits() {
        for (Chunk chunk : entityLimitsPerChunk.keySet()) {
            Map<EntityType, Integer> limits = entityLimitsPerChunk.get(chunk);
            Map<EntityType, Integer> counts = entityTypeCounts.getOrDefault(chunk, new HashMap<>());
            limits.forEach((type, limit) -> {
                int count = counts.getOrDefault(type, 0);
                if (count > limit) {
                    int toRemove = count - limit;
                    for (Entity entity : chunk.getEntities()) {
                        if (toRemove <= 0) break;
                        if (entity.getType() == type) {
                            entity.remove();
                            counts.put(type, counts.get(type) - 1);
                            entityCountsPerChunk.compute(chunk, (k, v) -> v - 1);
                            toRemove--;
                        }
                    }
                    if (settings.getConfig().getBoolean("modules.EntityLimiter.values.alerts")) {
                        long now = System.currentTimeMillis();
                        String key = chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
                        if (!lastMessageTime.containsKey(key) || now - lastMessageTime.get(key) >= 300000) {
                            String message = ChatColor.translateAlternateColorCodes('&',
                                    plugin.getLanguageManager().getMessage("entities_removed")
                                            .replace("%count%", String.valueOf(toRemove))
                                            .replace("%world%", chunk.getWorld().getName())
                                            .replace("%chunk_x%", String.valueOf(chunk.getX()))
                                            .replace("%chunk_z%", String.valueOf(chunk.getZ())));
                            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                                message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, message);
                            }
                            MessageUtil.broadcast(message);
                            lastMessageTime.put(key, now);
                        }
                    }
                }
            });
        }
    }

    public void handleEntitySpawn(EntitySpawnEvent event) {
        Chunk chunk = event.getLocation().getChunk();
        EntityType type = event.getEntityType();

        if (entityLimitsPerChunk.containsKey(chunk)) {
            Map<EntityType, Integer> limits = entityLimitsPerChunk.get(chunk);
            int limit = limits.getOrDefault(type, Integer.MAX_VALUE);
            Map<EntityType, Integer> counts = entityTypeCounts.computeIfAbsent(chunk, k -> new HashMap<>());
            int count = counts.getOrDefault(type, 0);
            if (count >= limit) {
                event.setCancelled(true);
                if (settings.getConfig().getBoolean("modules.EntityLimiter.values.alerts")) {
                    long now = System.currentTimeMillis();
                    String key = chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
                    if (!lastMessageTime.containsKey(key) || now - lastMessageTime.get(key) >= 300000) {
                        String message = ChatColor.translateAlternateColorCodes('&',
                                plugin.getLanguageManager().getMessage("entity_limit_reached")
                                        .replace("%world%", chunk.getWorld().getName())
                                        .replace("%type%", type.name())
                                        .replace("%limit%", String.valueOf(limit)));
                        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                            message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, message);
                        }
                        MessageUtil.broadcast(message);
                        lastMessageTime.put(key, now);
                    }
                }
            } else {
                counts.put(type, count + 1);
                entityCountsPerChunk.compute(chunk, (k, v) -> v == null ? 1 : v + 1);
            }
        }
    }

    public Map<Chunk, Integer> getEntityCountsPerChunk() {
        return entityCountsPerChunk;
    }
}