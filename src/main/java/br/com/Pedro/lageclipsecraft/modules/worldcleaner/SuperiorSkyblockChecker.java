package br.com.Pedro.lageclipsecraft.modules.worldcleaner;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SuperiorSkyblockChecker implements IslandChecker {
    @Override
    public boolean isInsideIsland(Location location) {
        try {
            Object island = Class.forName("com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI")
                    .getMethod("getIslandAt", Location.class)
                    .invoke(null, location.getWorld().getSpawnLocation());
            return island != null && (boolean) island.getClass().getMethod("isInside", Location.class)
                    .invoke(island, location);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2") != null;
    }
}