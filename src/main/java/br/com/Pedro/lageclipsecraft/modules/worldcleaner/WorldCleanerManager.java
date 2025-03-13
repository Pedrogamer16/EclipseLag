package br.com.Pedro.lageclipsecraft.modules.worldcleaner;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.config.Settings;
import br.com.Pedro.lageclipsecraft.utils.ActionBarUtil;
import br.com.Pedro.lageclipsecraft.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldCleanerManager {

    private static final Logger LOGGER = Logger.getLogger("LagEclipseCraft");

    private final LagEclipseCraft plugin;
    private final Settings settings;
    private final Inventory abyss = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "Abyss");
    private final Set<String> entityBlacklist;
    private final IslandChecker islandChecker = new SuperiorSkyblockChecker();
    private long tickCounter = 0;

    public WorldCleanerManager(LagEclipseCraft plugin, Settings settings) {
        this.plugin = plugin;
        this.settings = settings;
        this.entityBlacklist = settings.entityBlacklist;
    }

    public void start() {
        if (!settings.worldCleanerEnabled) {
            LOGGER.info(ChatColor.YELLOW + "WorldCleaner está desativado no config.yml.");
            return;
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            tickCounter += 20;
            handleWorldCleanup(settings.worldCleanerInterval);
            handleItemAndMobCleanup(settings.worldCleanerItemInterval);
        }, 0L, 20L);
    }

    private void handleWorldCleanup(long intervalTicks) {
        long ticksRemaining = intervalTicks - (tickCounter % intervalTicks);
        if (ticksRemaining <= 0) {
            scheduleCountdown(10, "clean_countdown", this::cleanWorlds);
        } else if (ticksRemaining == 10 * 60 * 20L) {
            broadcastMessage("clean_warning_10m", false, "%minutes%", "10");
        } else if (ticksRemaining == 5 * 60 * 20L) {
            broadcastMessage("clean_warning_5m", false, "%minutes%", "5");
        }
    }

    private void handleItemAndMobCleanup(long intervalTicks) {
        if (tickCounter % intervalTicks == 0) {
            Bukkit.getScheduler().runTask(plugin, this::cleanItemsAndMobs);
        }
    }

    private void scheduleCountdown(int seconds, String messageKey, Runnable onComplete) {
        AtomicInteger countdown = new AtomicInteger(seconds);
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            int sec = countdown.getAndDecrement();
            if (sec > 0) {
                broadcastMessage(messageKey, true, "%seconds%", String.valueOf(sec));
            } else {
                onComplete.run();
                optimizeAfterCleanup();
                task.cancel();
            }
        }, 0L, 20L);
    }

    private void broadcastMessage(String key, boolean actionBar, String... placeholders) {
        String message = plugin.getLanguageManager().getMessage(key);
        if (message == null || message.isEmpty()) {
            LOGGER.log(Level.WARNING, "Mensagem para '{0}' não encontrada.", key);
            return;
        }
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, message);
        }
        if (actionBar) {
            String finalMessage = message;
            Bukkit.getOnlinePlayers().forEach(player -> ActionBarUtil.sendActionBar(player, finalMessage));
        } else {
            MessageUtil.broadcast(message);
        }
        if (settings.debugMode) {
            LOGGER.log(Level.FINE, "Mensagem enviada: {0}", message);
        }
    }

    private void cleanWorlds() {
        List<String> allowedWorlds = settings.getConfig().getStringList("modules.WorldCleaner.values.allowed_worlds");
        for (World world : Bukkit.getWorlds()) {
            if (allowedWorlds.contains("*") || allowedWorlds.contains(world.getName())) {
                Bukkit.getPluginManager().callEvent(new CleanupEvent(world));
                cleanCreatures(world);
                cleanProjectiles(world);
            }
        }
        broadcastMessage("clean_complete", false, "%worlds%", String.join(", ", allowedWorlds));
    }

    public void cleanItemsAndMobs() {
        List<String> allowedWorlds = settings.getConfig().getStringList("modules.WorldCleaner.values.allowed_worlds");
        for (World world : Bukkit.getWorlds()) {
            if (allowedWorlds.contains("*") || allowedWorlds.contains(world.getName())) {
                cleanItems(world);
                cleanMobs(world);
            }
        }
    }

    private void cleanItems(World world) {
        if (!settings.getConfig().getBoolean("modules.WorldCleaner.values.items.enabled")) return;
        long timeLived = settings.getConfig().getLong("modules.WorldCleaner.values.items.time_lived", 10000);
        boolean useAbyss = settings.getConfig().getBoolean("modules.WorldCleaner.values.items.abyss.enabled");
        int removed = 0;

        for (Item entity : world.getEntitiesByClass(Item.class)) {
            if (entity.getTicksLived() * 50 < timeLived || entityBlacklist.contains(entity.getType().name().toUpperCase())) {
                continue;
            }
            if (islandChecker.isEnabled() && !islandChecker.isInsideIsland(entity.getLocation())) {
                continue;
            }
            if (useAbyss) abyss.addItem(entity.getItemStack());
            entity.remove();
            removed++;
        }

        if (removed > 0 && settings.getConfig().getBoolean("modules.WorldCleaner.values.alerts")) {
            broadcastMessage("items_cleared", false, "%world%", world.getName(), "%count%", String.valueOf(removed));
        }
    }

    private void cleanMobs(World world) {
        if (!settings.getConfig().getBoolean("modules.WorldCleaner.values.mobs.enabled", true)) return;
        int removed = 0;

        for (LivingEntity entity : world.getLivingEntities()) {
            if (entityBlacklist.contains(entity.getType().name().toUpperCase())) {
                continue;
            }
            entity.remove();
            removed++;
        }

        if (removed > 0 && settings.getConfig().getBoolean("modules.WorldCleaner.values.alerts")) {
            broadcastMessage("mobs_cleared", false, "%world%", world.getName(), "%count%", String.valueOf(removed));
        }
    }

    private void cleanCreatures(World world) {
        if (!settings.getConfig().getBoolean("modules.WorldCleaner.values.creatures.enabled")) return;
        List<String> allowed = settings.getConfig().getStringList("modules.WorldCleaner.values.creatures.list");
        boolean removeRoseStacker = settings.getConfig().getBoolean("modules.WorldCleaner.values.rosestacker_remove");
        boolean isRoseStacker = plugin.getServer().getPluginManager().getPlugin("RoseStacker") != null;

        for (LivingEntity entity : world.getLivingEntities()) {
            if (!allowed.contains(entity.getType().name().toUpperCase()) ||
                    entityBlacklist.contains(entity.getType().name().toUpperCase()) ||
                    (isRoseStacker && !removeRoseStacker && entity.hasMetadata("rosestacker_stacked_entity"))) {
                continue;
            }
            entity.remove();
        }
    }

    private void cleanProjectiles(World world) {
        if (!settings.getConfig().getBoolean("modules.WorldCleaner.values.projectiles.enabled")) return;
        for (Entity entity : world.getEntitiesByClasses(EntityType.ARROW.getEntityClass(), EntityType.SNOWBALL.getEntityClass())) {
            if (entityBlacklist.contains(entity.getType().name().toUpperCase())) {
                continue;
            }
            entity.remove();
        }
    }

    private void optimizeAfterCleanup() {
        if (settings.getConfig().getBoolean("main.optimize_memory", true)) {
            System.gc();
            LOGGER.info(ChatColor.GRAY + "[LagEclipseCraft] Otimização de memória realizada.");
        }
    }

    public Inventory getAbyss() { return abyss; }
}