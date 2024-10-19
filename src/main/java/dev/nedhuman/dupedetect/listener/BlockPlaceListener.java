package dev.nedhuman.dupedetect.listener;

import dev.nedhuman.dupedetect.DupeDetect;
import dev.nedhuman.dupedetect.Utils;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(
                DupeDetect.getInstance().isPersistOverPlace() &&
                DupeDetect.getInstance().detectThisItem(event.getItemInHand().getType()) &&
                event.getBlock().getState() instanceof TileState state
        ) {
            ItemStack item = event.getItemInHand();

            if(!Utils.hasIdentification(item.getItemMeta().getPersistentDataContainer())) {
                Utils.applyIdentification(item);
            }

            Utils.applyIdentification(state, Utils.getIdentification(item.getItemMeta().getPersistentDataContainer()));
        }
    }
}
