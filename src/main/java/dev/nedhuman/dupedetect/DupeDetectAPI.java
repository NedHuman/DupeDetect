package dev.nedhuman.dupedetect;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

/**
 * Class for methods which allow for interacting with the DupeDetect plugin
 *
 * @author NedHuman
 */
public final class DupeDetectAPI {

    private DupeDetectAPI() {}

    /**
     * Apply a 64-bit random identifier to an item
     * @param item the item to apply the ID to
     * @throws IllegalArgumentException if the item is air
     */
    public static void applyIdentification(ItemStack item) throws IllegalArgumentException {
        if(item.getType() == Material.AIR) {
            throw new IllegalArgumentException("Cannot apply identification to air");
        }
        Utils.applyIdentification(item);
    }

    /**
     * Apply a 64-bit identifier to an item
     * @param item the item to apply the ID to
     * @param id the identifier to apply
     * @throws IllegalArgumentException if the item is air
     */
    public static void applyIdentification(ItemStack item, long id) throws IllegalArgumentException {
        if(item.getType() == Material.AIR) {
            throw new IllegalArgumentException("Cannot apply identification to air");
        }
        Utils.applyIdentification(item, id);
    }

    /**
     * Check if a certain {@link PersistentDataContainer} contains a DupeDetect identifier.
     * @param pdc the {@link PersistentDataContainer} to check
     * @return true if the PDC has a DupeDetect identifier
     */
    public static boolean hasIdentification(PersistentDataContainer pdc) {
        return Utils.hasIdentification(pdc);
    }

    /**
     * Get the identifier of a certain {@link PersistentDataContainer}
     * @param pdc the {@link PersistentDataContainer} to get from
     * @return the identifier
     * @throws IllegalArgumentException if the pdc has no identifier. Check with hasIdentification beforehand.
     */
    public static long getIdentification(PersistentDataContainer pdc) throws IllegalArgumentException {
        return Utils.getIdentification(pdc);
    }

    /**
     * Check if a certain player bypasses the DupeDetect checker
     * @param player the player to check
     * @return true if yes
     */
    public static boolean bypassesDupeDetect(Player player) {
        return Utils.bypassesDupeDetect(player);
    }

    /**
     * Perform a manual search on an inventory for illegal items.
     * @param inventory the inventory to search
     * @param player the player to blame, if anything is found
     */
    public static void checkInventory(Inventory inventory, Player player) {
        Utils.checkInventory(inventory, player);
    }

    /**
     * Manually trigger an alert that a duped item has been found in this player's posession
     * @param player the player
     * @param item the item
     * @param id the item ID
     */
    public static void triggerDupeFound(Player player, ItemStack item, long id) {
        Utils.triggerDupeFound(player, item, id);
    }
}
