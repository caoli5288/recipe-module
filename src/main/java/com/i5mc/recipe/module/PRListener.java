package com.i5mc.recipe.module;

import mc.mcgrizzz.prorecipes.events.WorkbenchCraftEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created on 17-1-27.
 */
public class PRListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void handle(WorkbenchCraftEvent event) {
        ItemStack item = event.getResult();
        Player p = event.getPlayer();
        int l = Main.getLimit(p, item);
        if (l > -1 && (l == 0 || l - event.getResult().getAmount() < 0)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void handle(InventoryClickEvent event) {
        Inventory click = event.getClickedInventory();
        if (!Main.nil(click) && event.getSlotType() == InventoryType.SlotType.RESULT) {
            ItemStack item = click.getItem(event.getSlot());
            HumanEntity who = event.getWhoClicked();
            if (!Main.nil(item) && item.getTypeId() > 0 && Main.getLimit(who, item) > -1) {
                click(event, who, item);
            }
        }
    }

    private void click(InventoryClickEvent event, HumanEntity who, ItemStack item) {
        if (event.isShiftClick()) {
            Main.messenger.send(who, "craft.shift", ChatColor.RED + "该合成不支持批量，请松开SHIFT键后重试");
            event.setCancelled(true);
        } else {
            int limit = Main.addLimit(who, item, item.getAmount());
            if (limit > -1) {
                String l = Main.messenger.find("craft.limit", ChatColor.GREEN + "此类物品合成限制剩余%d次");
                who.sendMessage(String.format(l, limit));
            }
        }
    }

}
