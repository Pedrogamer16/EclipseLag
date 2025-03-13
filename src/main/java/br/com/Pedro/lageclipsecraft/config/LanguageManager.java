package br.com.Pedro.lageclipsecraft.config;

import br.com.Pedro.lageclipsecraft.LagEclipseCraft;
import br.com.Pedro.lageclipsecraft.utils.LogUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LanguageManager {

    private final LagEclipseCraft plugin;
    private final String language;
    private YamlConfiguration langConfig;

    public LanguageManager(LagEclipseCraft plugin, String language) {
        this.plugin = plugin;
        this.language = language;
        loadLanguage();
    }

    public void loadLanguage() {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();

        File langFile = new File(langDir, language + ".yml");
        if (!langFile.exists()) {
            saveDefaultLanguage(langFile);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public void reloadLanguage() {
        loadLanguage();
    }

    public String getMessage(String key, String... placeholders) {
        String message = langConfig.getString(key, "&cMensagem não encontrada: " + key);
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private void saveDefaultLanguage(File langFile) {
        try {
            langFile.createNewFile();
            YamlConfiguration defaultLang = YamlConfiguration.loadConfiguration(langFile);
            defaultLang.set("no_permission", "&cVocê não tem permissão para usar este comando!");
            defaultLang.set("invalid_command", "&cComando inválido! Use /eclipselag <clear|reload|info|map|abyss|profile|config|stats>");
            defaultLang.set("entities_cleared", "&aEntidades verificadas e limpas!");
            defaultLang.set("config_reloaded", "&aConfiguração recarregada!");
            defaultLang.set("tps_info", "&eTPS atual: %tps%");
            defaultLang.set("world_info", "&e%world%: %count% entidades");
            defaultLang.set("entity_limit_reached", "&c&l[Limiter] &fLimite de &e%type% &fatingido (&e%limit%&f) em &e%world%!");
            defaultLang.set("entities_removed", "&a&l[Limiter] &fRemovidas &e%count% entidades em &e%world% (chunk &e%chunk_x%, %chunk_z%&f)");
            defaultLang.set("items_cleared", "&a&l[Cleaner] &fRemovidos &e%count% itens em &e%world%!");
            defaultLang.set("mobs_cleared", "&a&l[Cleaner] &fRemovidos &e%count% mobs em &e%world%!");
            defaultLang.set("tps_low", "&cTPS baixo: %tps%");
            defaultLang.set("clean_warning_10m", "&e&l[Aviso] &fLimpeza geral em &a10 minutos&f!");
            defaultLang.set("clean_warning_5m", "&e&l[Aviso] &fLimpeza geral em &a5 minutos&f!");
            defaultLang.set("clean_countdown", "&e&lLimpeza em &c%seconds%s &e&lno action bar!");
            defaultLang.set("clean_complete", "&a&l[Cleaner] &fMundos limpos: &e%worlds%");
            defaultLang.set("redstone_limit_exceeded", "&cLimite de redstone por chunk atingido!");
            defaultLang.set("minecart_limit_exceeded", "&cLimite de minecarts por chunk atingido!");
            defaultLang.set("config_error", "&cErro ao configurar: %error%");
            defaultLang.set("permission_denied", "&cVocê não tem permissão para %perm%!");
            defaultLang.save(langFile);
        } catch (IOException e) {
            LogUtil.warn("Falha ao criar " + language + ".yml: " + e.getMessage());
        }
    }
}