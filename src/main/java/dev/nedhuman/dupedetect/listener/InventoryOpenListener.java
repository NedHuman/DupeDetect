package dev.nedhuman.dupedetect.listener;

import dev.nedhuman.dupedetect.DupeDetect;
import dev.nedhuman.dupedetect.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryOpenListener implements Listener {
    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if(
                event.getPlayer() instanceof Player player &&
                DupeDetect.getInstance().shouldInventoryCheck() &&
                !Utils.bypassesDupeDetect(player)
        ) {
            Utils.checkInventory(event.getInventory(), player);
        }
    }
}
