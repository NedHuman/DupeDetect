package dev.nedhuman.dupedetect;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PeriodicalChecker extends BukkitRunnable {

    @Override
    public void run() {
        for(Player i : Bukkit.getOnlinePlayers()) {
            if(Utils.bypassesDupeDetect(i)) {
                continue;
            }
            Utils.checkInventory(i.getInventory(), i);
        }
    }
}
