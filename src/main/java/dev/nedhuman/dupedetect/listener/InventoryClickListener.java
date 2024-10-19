package dev.nedhuman.dupedetect.listener;

import dev.nedhuman.dupedetect.DupeAlert;
import dev.nedhuman.dupedetect.DupeDetect;
import dev.nedhuman.dupedetect.Utils;
import dev.nedhuman.dupedetect.menu.DupeDetectMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // Inventory clicks in regard to applying ID
        if(event.getCurrentItem() != null && DupeDetect.getInstance().detectThisItem(event.getCurrentItem().getType())) {
            if(!Utils.hasIdentification(event.getCurrentItem().getItemMeta().getPersistentDataContainer())) {
                Utils.applyIdentification(event.getCurrentItem());
            }
        }

        // here's inventory click in regard to the dupe detect menu
        Inventory inventory = event.getInventory();
        if(!(inventory.getHolder() instanceof DupeDetectMenu menu)) {
            return;
        }
        event.setCancelled(true);
        int slot = event.getSlot();
        if(slot == 53) {
            if(menu.getNextPage() != -1) {
                Inventory inv = new DupeDetectMenu(player, menu.getNextPage()+1).getInventory();
                player.openInventory(inv);
                return;
            }else{
                player.sendMessage(ChatColor.RED+"No next page found");
            }
        }
        if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            PersistentDataContainer pdc = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
            if(pdc.has(DupeDetectMenu.ID, PersistentDataType.INTEGER)) {
                int id = pdc.get(DupeDetectMenu.ID, PersistentDataType.INTEGER);
                DupeAlert alert = DupeDetect.getInstance().findDupeAlert(id);
                if(alert == null) {
                    player.sendMessage(ChatColor.RED+"Internal Error: invalid dupe alert ID");
                    return;
                }
                if(event.getClick().isRightClick()) {
                    player.teleport(alert.location());
                }else if(event.getClick().isLeftClick() && player.hasPermission("dupedetect.getcopy")) {
                    player.getInventory().addItem(alert.item());
                }
            }
        }
    }
}
