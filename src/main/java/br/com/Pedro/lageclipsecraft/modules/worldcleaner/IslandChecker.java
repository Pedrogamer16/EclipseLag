package br.com.Pedro.lageclipsecraft.modules.worldcleaner;

import org.bukkit.Location;

public interface IslandChecker {
    boolean isInsideIsland(Location location);
    boolean isEnabled();
}