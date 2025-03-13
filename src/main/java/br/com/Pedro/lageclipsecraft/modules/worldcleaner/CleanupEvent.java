package br.com.Pedro.lageclipsecraft.modules.worldcleaner;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CleanupEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final World world;

    public CleanupEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}