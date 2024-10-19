package dev.nedhuman.dupedetect.listener;

import dev.nedhuman.dupedetect.DupeDetect;
import dev.nedhuman.dupedetect.Utils;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

public class BlockDropItemListener implements Listener {
    @EventHandler
    public void onDrop(BlockDropItemEvent event) {
        if(
                DupeDetect.getInstance().isPersistOverPlace() &&
                DupeDetect.getInstance().detectThisItem(event.getBlockState().getType()) &&
                event.getBlockState() instanceof TileState state &&
                Utils.hasIdentification(state.getPersistentDataContainer())
        ) {
            long id = Utils.getIdentification(state.getPersistentDataContainer());

            for(Item i : event.getItems()) { // Doing a loop, but there should only be one item. Hopefully
                Utils.applyIdentification(i.getItemStack(), id);
                break; // breaking just incase for some reason theres multiple
            }
        }
    }
}
