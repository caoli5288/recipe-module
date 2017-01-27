package com.i5mc.recipe.module;

import mc.mcgrizzz.prorecipes.events.MulticraftEvent;
import mc.mcgrizzz.prorecipes.events.WorkbenchCraftEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Created on 17-1-27.
 */
public class PRListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void handle(WorkbenchCraftEvent event) {
        Player p = event.getPlayer();
        ItemStack result = event.getResult();
        if (Main.limit(p, result)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void handle(MulticraftEvent event) {
        Player p = event.getPlayer();
        ItemStack[] result = event.getResults();
        int len = result.length;
        int i = 0;
        while (!event.isCancelled() && i < len) {
            if (Main.limit(p, result[i++])) {
                event.setCancelled(true);
            }
        }
    }

}
