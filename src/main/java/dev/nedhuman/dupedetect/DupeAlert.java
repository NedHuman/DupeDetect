package dev.nedhuman.dupedetect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.UUID;

/**
 * Record which holds information about a dupe alert
 */
public record DupeAlert(
        UUID player,
        ItemStack item,
        long id,
        Location location,
        int uuid,
        Date time
)
{
    public OfflinePlayer getPlayerObject() {
        return Bukkit.getOfflinePlayer(player);
    }
}
