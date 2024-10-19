package dev.nedhuman.dupedetect.command;

import dev.nedhuman.dupedetect.DupeDetect;
import dev.nedhuman.dupedetect.menu.DupeDetectMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DupeDetectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return true;
        }
        if(player.hasPermission("dupedetect.menu")) {
            player.openInventory(new DupeDetectMenu(player, DupeDetect.getInstance().getDupeAlerts().size()).getInventory());
        }

        return true;
    }
}
