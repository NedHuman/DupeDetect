package dev.nedhuman.dupedetect;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.*;

public class Utils {

    private Utils() {}

    public static final Random RANDOM = new Random();
    public static final NamespacedKey ITEM_ID = new NamespacedKey(DupeDetect.getInstance(), "itemid");

    public static void applyIdentification(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer(); // says might be null, but it wont
        pdc.set(ITEM_ID, PersistentDataType.LONG, RANDOM.nextLong());
        item.setItemMeta(meta);
    }

    public static void applyIdentification(ItemStack item, long id) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer(); // says might be null, but it wont
        pdc.set(ITEM_ID, PersistentDataType.LONG, id);
        item.setItemMeta(meta);
    }

    public static void applyIdentification(TileState state, long id) {
        PersistentDataContainer pdc = state.getPersistentDataContainer();
        pdc.set(ITEM_ID, PersistentDataType.LONG, id);
        state.update();
    }

    public static boolean hasIdentification(PersistentDataContainer pdc) {
        return pdc.has(ITEM_ID, PersistentDataType.LONG);
    }

    public static long getIdentification(PersistentDataContainer pdc) {
        if(!pdc.has(ITEM_ID, PersistentDataType.LONG)) {
            throw new IllegalArgumentException("Attempted to get identification of item with no ID");
        }
        return pdc.get(ITEM_ID, PersistentDataType.LONG);
    }

    public static void checkInventory(Inventory inventory, Player player) {

        Set<Long> idsFound = new HashSet<>();

        for(ItemStack i : inventory.getContents()) {
            if(i != null && DupeDetect.getInstance().detectThisItem(i.getType())) {
                if(hasIdentification(i.getItemMeta().getPersistentDataContainer())) {
                    long id = getIdentification(i.getItemMeta().getPersistentDataContainer());
                    if(!idsFound.add(id)) {
                        triggerDupeFound(player, i, id);
                        return;
                    }
                }else{
                    applyIdentification(i);
                }
            }
        }
    }

    public static String formatLocation(Location location) {
        return location.getBlockX()+", "+location.getBlockY()+", "+ location.getBlockZ()+" in "+location.getWorld().getName();
    }

    protected static void triggerDupeFound(Player player, ItemStack item, long id) {

        // Register the dupe
        DupeDetect.getInstance().getDupeAlerts().add(new DupeAlert(player.getUniqueId(), item.clone(), id, player.getLocation(), RANDOM.nextInt(), new Date()));

        TextComponent message = new TextComponent(ChatColor.DARK_RED+"[!] "+ChatColor.RED+player.getName()+" found with two identical item IDs");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(
                ChatColor.YELLOW+"Location: "+ChatColor.GOLD+formatLocation(player.getLocation())+
                ChatColor.YELLOW+"\nItem ID: "+ChatColor.GOLD+Long.toHexString(id)+
                ChatColor.YELLOW+"\nItem: "+ChatColor.GOLD+item.getType())
        } ));

        int alertPileup = 0;
        if(DupeDetect.getInstance().isAntiSpam()) {
            alertPileup = DupeDetect.getInstance().getAlertPileup().getOrDefault(id, 0);
            if(alertPileup < 10) {
                DupeDetect.getInstance().getAlertPileup().put(id, alertPileup+1);
            }else{
                DupeDetect.getInstance().getAlertPileup().put(id, 0);
            }
        }
        if(alertPileup == 0) {
            for (Player i : Bukkit.getOnlinePlayers()) {
                if (i.hasPermission("dupedetect.notify")) {
                    i.spigot().sendMessage(message);
                }
            }
        }

        DupeDetect.getInstance().getLogger().warning("[!] "+player.getName()+" found with two identical item IDs at "+formatLocation(player.getLocation()));

        String webhookURL = DupeDetect.getInstance().getDiscordWebhookUrl();
        if(webhookURL != null) {
            DiscordWebhook hook = new DiscordWebhook(webhookURL);
            hook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle("DupeDetect")
                    .setColor(java.awt.Color.RED)
                    .setDescription("[!] "+player.getName()+" found with two identical item IDs at "+formatLocation(player.getLocation())));

            Bukkit.getScheduler().runTaskAsynchronously(DupeDetect.getInstance(), () -> {
                try {
                    hook.execute();
                }catch (IOException e) {
                    DupeDetect.getInstance().getLogger().warning("IOException while executing discord webhook:");
                    e.printStackTrace();
                }
            });
        }
    }

    public static boolean bypassesDupeDetect(Player player) {
        if(player.getGameMode() == GameMode.CREATIVE && DupeDetect.getInstance().shouldIgnoreCreative()) {
            return true;
        }
        return player.hasPermission("dupedetect.bypass") || DupeDetect.getInstance().getBypassPlayers().contains(player.getName());
    }
}
