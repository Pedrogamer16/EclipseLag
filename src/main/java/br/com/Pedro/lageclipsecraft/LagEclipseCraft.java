package br.com.Pedro.lageclipsecraft;

import br.com.Pedro.lageclipsecraft.commands.EclipseCraftCommand;
import br.com.Pedro.lageclipsecraft.commands.EclipseLagCommand;
import br.com.Pedro.lageclipsecraft.config.ConfigManager;
import br.com.Pedro.lageclipsecraft.config.LanguageManager;
import br.com.Pedro.lageclipsecraft.config.Settings;
import br.com.Pedro.lageclipsecraft.listeners.*;
import br.com.Pedro.lageclipsecraft.modules.customai.CustomAiManager;
import br.com.Pedro.lageclipsecraft.modules.customvehicles.CustomVehiclesManager;
import br.com.Pedro.lageclipsecraft.modules.entitylimiter.EntityLimiterManager;
import br.com.Pedro.lageclipsecraft.modules.lagmonitor.LagMonitorManager;
import br.com.Pedro.lageclipsecraft.modules.consolefilter.ConsoleFilterManager;
import br.com.Pedro.lageclipsecraft.modules.redstonelimiter.RedstoneLimiterManager;
import br.com.Pedro.lageclipsecraft.modules.worldcleaner.WorldCleanerManager;
import br.com.Pedro.lageclipsecraft.modules.instantleafdecay.InstantLeafDecayManager;
import br.com.Pedro.lageclipsecraft.utils.LogUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class LagEclipseCraft extends JavaPlugin {

    private ConfigManager configManager;
    private LanguageManager languageManager;
    private Settings settings;
    private CustomAiManager customAiManager;
    private CustomVehiclesManager customVehiclesManager;
    private EntityLimiterManager entityLimiterManager;
    private LagMonitorManager lagMonitorManager;
    private ConsoleFilterManager consoleFilterManager;
    private RedstoneLimiterManager redstoneLimiterManager;
    private WorldCleanerManager worldCleanerManager;
    private InstantLeafDecayManager instantLeafDecayManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        try {
            configManager.loadConfig();
            settings = new Settings(configManager);
        } catch (Exception e) {
            getLogger().severe(ChatColor.RED + "Falha ao carregar o config.yml: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        languageManager = new LanguageManager(this, settings.language);
        languageManager.loadLanguage();

        registerCommand("eclipselag", new EclipseLagCommand(this), Arrays.asList("clear", "reload", "info", "map", "abyss", "profile", "config", "stats"));
        registerCommand("eclipsecraft", new EclipseCraftCommand(this), Arrays.asList("version", "help"));

        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new RedstoneListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);

        customAiManager = new CustomAiManager(this);
        customVehiclesManager = new CustomVehiclesManager(this);
        entityLimiterManager = new EntityLimiterManager(this, settings);
        lagMonitorManager = new LagMonitorManager(this);
        consoleFilterManager = new ConsoleFilterManager(this);
        redstoneLimiterManager = new RedstoneLimiterManager(this);
        worldCleanerManager = new WorldCleanerManager(this, settings);
        instantLeafDecayManager = new InstantLeafDecayManager(this);

        customAiManager.start();
        customVehiclesManager.start();
        entityLimiterManager.start();
        lagMonitorManager.start();
        consoleFilterManager.start();
        redstoneLimiterManager.start();
        worldCleanerManager.start();
        instantLeafDecayManager.start();

        checkPluginCompatibility();
        startResourceOptimizer();

        LogUtil.log(ChatColor.GREEN + "LagEclipseCraft habilitado com sucesso!");
        if (settings.debugMode) {
            getLogger().log(Level.FINE, "Modo debug ativado.");
        }
    }

    private void registerCommand(String name, CommandExecutor executor, List<String> completions) {
        PluginCommand command = getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter((sender, cmd, alias, args) -> {
                if (args.length == 1 && sender.hasPermission(name + ".use")) {
                    return completions;
                }
                return null;
            });
            command.setPermission(name + ".use");
            command.setPermissionMessage(ChatColor.translateAlternateColorCodes('&', settings.getConfig().getString("no_permission")));
        }
    }

    private void checkPluginCompatibility() {
        if (getServer().getPluginManager().getPlugin("SuperiorSkyblock2") != null) {
            LogUtil.log(ChatColor.YELLOW + "Compatibilidade com SuperiorSkyblock2 detectada!");
        }
        if (getServer().getPluginManager().getPlugin("RoseStacker") != null) {
            LogUtil.log(ChatColor.YELLOW + "Compatibilidade com RoseStacker detectada!");
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            LogUtil.log(ChatColor.YELLOW + "Compatibilidade com PlaceholderAPI detectada!");
        }
    }

    private void startResourceOptimizer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                optimizeResources();
            }
        }.runTaskTimer(this, 0L, settings.getConfig().getInt("main.resource_optimization_interval", 600) * 20L);
    }

    private void optimizeResources() {
        if (settings.getConfig().getBoolean("main.optimize_memory", true)) {
            System.gc();
            LogUtil.log(ChatColor.GRAY + "Garbage collection executado para otimizar memória.");
        }
        if (settings.getConfig().getBoolean("main.unload_inactive_chunks", true)) {
            getServer().getWorlds().forEach(world -> {
                for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
                    if (!chunk.isEntitiesLoaded()) {
                        chunk.unload(true);
                    }
                }
            });
            LogUtil.log(ChatColor.GRAY + "Chunks inativos descarregados.");
        }
    }

    @Override
    public void onDisable() {
        optimizeResources();
        LogUtil.log(ChatColor.RED + "LagEclipseCraft desabilitado!");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public LanguageManager getLanguageManager() { return languageManager; }
    public Settings getSettings() { return settings; }
    public CustomAiManager getCustomAiManager() { return customAiManager; }
    public CustomVehiclesManager getCustomVehiclesManager() { return customVehiclesManager; }
    public EntityLimiterManager getEntityLimiterManager() { return entityLimiterManager; }
    public LagMonitorManager getLagMonitorManager() { return lagMonitorManager; }
    public ConsoleFilterManager getConsoleFilterManager() { return consoleFilterManager; }
    public RedstoneLimiterManager getRedstoneLimiterManager() { return redstoneLimiterManager; }
    public WorldCleanerManager getWorldCleanerManager() { return worldCleanerManager; }
    public InstantLeafDecayManager getInstantLeafDecayManager() { return instantLeafDecayManager; }
}