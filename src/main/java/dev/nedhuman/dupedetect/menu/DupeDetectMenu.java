package dev.nedhuman.dupedetect.menu;

import dev.nedhuman.dupedetect.DupeAlert;
import dev.nedhuman.dupedetect.DupeDetect;
import dev.nedhuman.dupedetect.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class DupeDetectMenu implements InventoryHolder {

    public static final NamespacedKey ID = new NamespacedKey(DupeDetect.getInstance(), "menuId");

    private final Inventory inventory;
    private int nextPage;

    public DupeDetectMenu(Player player, int fromIndex) {
        inventory = Bukkit.createInventory(this, 54);
        List<DupeAlert> alerts = DupeDetect.getInstance().getDupeAlerts();

        nextPage = fromIndex;
        int increment = -1;
        while(--nextPage >= 0 && ++increment < 45) {
            DupeAlert alert = alerts.get(nextPage);

            ItemStack item = new ItemStack(alert.item().getType());
            ItemMeta meta = item.getItemMeta();
            assert meta != null; // wont happen but ill do it cus that yellow underline looks ugly
            meta.setDisplayName(ChatColor.DARK_RED+"[!] "+ChatColor.RED+alert.getPlayerObject().getName()+" found with two identical item IDs");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW+"Location: "+ChatColor.GOLD+ Utils.formatLocation(alert.location()));
            lore.add(ChatColor.YELLOW+"Item ID: "+ChatColor.GOLD+Long.toHexString(alert.id()));
            lore.add(ChatColor.YELLOW+"Item: "+ChatColor.GOLD+alert.item().getType());
            lore.add(ChatColor.YELLOW+"Time: "+ChatColor.GOLD+alert.time());
            lore.add(ChatColor.YELLOW+"Right-Click to teleport to the location of the alert");
            if(player.hasPermission("dupedetect.getcopy")) {
                lore.add(ChatColor.YELLOW+"Left-Click to receive a copy of the item");
            }
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(ID, PersistentDataType.INTEGER, alert.uuid());

            item.setItemMeta(meta);

            inventory.setItem(increment, item);
        }

        ItemStack arrowRight = new ItemStack(Material.ARROW);
        ItemMeta meta = arrowRight.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.YELLOW+"Next page");
        arrowRight.setItemMeta(meta);
        inventory.setItem(53, arrowRight);
    }

    public int getNextPage() {
        return nextPage;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
