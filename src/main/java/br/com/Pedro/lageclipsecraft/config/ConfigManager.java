package br.com.Pedro.lageclipsecraft.config;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.utils.LogUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {

    private final LagEclipseCraft plugin;
    private FileConfiguration config;
    private File configFile;
    private String prefix;

    public ConfigManager(LagEclipseCraft plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                plugin.saveResource("config.yml", false); // Salva o config.yml padrão do JAR
                plugin.getLogger().info("Arquivo config.yml criado a partir do recurso embutido.");
            } catch (IllegalArgumentException e) {
                plugin.getLogger().severe("Não foi possível encontrar o config.yml embutido no JAR: " + e.getMessage());
                return; // Evita prosseguir com config nulo
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("main.prefix", "&8[&e&l⚡&8] "));
        validateConfig();
    }

    public void reloadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("main.prefix", "&8[&e&l⚡&8] "));
        validateConfig();
    }

    private void validateConfig() {
        int interval = config.getInt("main.monitor_interval", 5);
        if (interval < 1) {
            LogUtil.warn("monitor_interval inválido! Definindo para 5.");
            config.set("main.monitor_interval", 5);
        }
        if (!config.isSet("modules.WorldCleaner.values.allowed_worlds")) {
            config.set("modules.WorldCleaner.values.allowed_worlds", List.of("*"));
        }
        if (!config.isSet("modules.WorldCleaner.values.blacklist")) {
            config.set("modules.WorldCleaner.values.blacklist.entities", List.of("ARMOR_STAND", "ITEM_FRAME"));
            config.set("modules.WorldCleaner.values.blacklist.keys", List.of("CHEST", "FURNACE"));
        }
        if (!config.isSet("modules.WorldCleaner.values.items.interval")) {
            config.set("modules.WorldCleaner.values.items.interval", 300);
        }
        if (!config.isSet("modules.WorldCleaner.values.rosestacker_remove")) {
            config.set("modules.WorldCleaner.values.rosestacker_remove", false);
        }
        if (!config.isSet("modules.EntityLimiter.values.limit_type")) {
            config.set("modules.EntityLimiter.values.limit_type", "world");
        }
        if (!config.isSet("main.optimize_memory")) {
            config.set("main.optimize_memory", true);
        }
        if (!config.isSet("main.optimize_cpu")) {
            config.set("main.optimize_cpu", true);
        }
        if (!config.isSet("main.resource_optimization_interval")) {
            config.set("main.resource_optimization_interval", 600);
        }
        if (!config.isSet("main.unload_inactive_chunks")) {
            config.set("main.unload_inactive_chunks", true);
        }
        saveConfig(); // Salva as alterações após validação
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            loadConfig(); // Recarrega se config for null
            if (config == null) {
                plugin.getLogger().severe("Configuração não carregada. Retornando null.");
            }
        }
        return config;
    }

    public String getPrefix() {
        return prefix;
    }

    public void saveConfig() {
        if (config == null || configFile == null) {
            plugin.getLogger().warning("Não há configuração para salvar.");
            return;
        }
        try {
            config.save(configFile);
            plugin.getLogger().info("Configuração salva com sucesso em config.yml.");
        } catch (IOException e) {
            plugin.getLogger().severe("Não foi possível salvar o config.yml: " + e.getMessage());
        }
    }
}