package br.com.Pedro.lageclipsecraft.utils;

import org.bukkit.Bukkit;

public class LogUtil {

    public static void log(String message) {
        Bukkit.getLogger().info("[LagEclipseCraft] " + message);
    }

    public static void warn(String message) {
        Bukkit.getLogger().warning("[LagEclipseCraft] " + message);
    }
}