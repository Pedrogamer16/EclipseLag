package br.com.Pedro.lageclipsecraft.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class Settings {
    private final ConfigManager configManager;
    public final boolean worldCleanerEnabled;
    public final long worldCleanerInterval;
    public final long worldCleanerItemInterval;
    public final boolean entityLimiterEnabled;
    public final Set<String> entityBlacklist;
    public final boolean debugMode;
    public final String language;

    public Settings(ConfigManager configManager) {
        this.configManager = configManager;
        FileConfiguration config = configManager.getConfig();
        this.worldCleanerEnabled = config.getBoolean("modules.WorldCleaner.enabled", true);
        this.worldCleanerInterval = config.getInt("modules.WorldCleaner.values.interval", 480) * 20L;
        this.worldCleanerItemInterval = config.getInt("modules.WorldCleaner.values.items.interval", 300) * 20L;
        this.entityLimiterEnabled = config.getBoolean("modules.EntityLimiter.enabled", true);
        this.entityBlacklist = new HashSet<>(config.getStringList("modules.WorldCleaner.values.blacklist.entities"));
        this.debugMode = config.getBoolean("main.debug", false);
        this.language = config.getString("main.language", "pt");
    }

    public FileConfiguration getConfig() {
        return configManager.getConfig();
    }
}