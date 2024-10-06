package dev.nedhuman.dupeDetect;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Utils {

    public static final Random RANDOM = new Random();
    public static final NamespacedKey ITEM_ID = new NamespacedKey(DupeDetect.getInstance(), "itemid");

    public static void applyIdentification(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer(); // says might be null, but it wont
        pdc.set(ITEM_ID, PersistentDataType.LONG, RANDOM.nextLong());
        item.setItemMeta(meta);
    }

    public static boolean hasIdentification(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(ITEM_ID, PersistentDataType.LONG);
    }

    private static long getIdentification(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer(); // says might be null, but it wont
        if(!pdc.has(ITEM_ID, PersistentDataType.LONG)) {
            throw new IllegalArgumentException("Attempted to get identification of item with no ID");
        }
        return pdc.get(ITEM_ID, PersistentDataType.LONG);
    }

    public static void checkInventory(Inventory inventory, Player player) {

        Set<Long> idsFound = new HashSet<>();

        for(ItemStack i : inventory.getContents()) {
            if(i != null && DupeDetect.getInstance().detectThisItem(i.getType())) {
                if(hasIdentification(i)) {
                    long id = getIdentification(i);
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

    private static String formatLocation(Location location) {
        return location.getBlockX()+", "+location.getBlockY()+", "+ location.getBlockZ()+" in "+location.getWorld().getName();
    }

    private static void triggerDupeFound(Player player, ItemStack item, long id) {
        TextComponent message = new TextComponent(ChatColor.DARK_RED+"[!] "+ChatColor.RED+player.getName()+" found with two identical item IDs");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(
                ChatColor.YELLOW+"Location: "+ChatColor.GOLD+formatLocation(player.getLocation())+
                ChatColor.YELLOW+"\nItem ID: "+ChatColor.GOLD+Long.toHexString(id)+
                ChatColor.YELLOW+"\nItem: "+ChatColor.GOLD+item.getType())
        } ));

        for(Player i : Bukkit.getOnlinePlayers()) {
            if(i.hasPermission("dupedetect.notify")) {
                i.spigot().sendMessage(message);
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
