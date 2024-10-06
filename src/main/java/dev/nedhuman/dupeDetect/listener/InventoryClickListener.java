package dev.nedhuman.dupeDetect.listener;

import dev.nedhuman.dupeDetect.DupeDetect;
import dev.nedhuman.dupeDetect.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getCurrentItem() != null && DupeDetect.getInstance().detectThisItem(event.getCurrentItem().getType())) {
            if(!Utils.hasIdentification(event.getCurrentItem())) {
                Utils.applyIdentification(event.getCurrentItem());
            }
        }
    }
}
