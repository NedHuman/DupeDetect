package dev.nedhuman.dupedetect;

import dev.nedhuman.dupedetect.command.DupeDetectCommand;
import dev.nedhuman.dupedetect.listener.BlockDropItemListener;
import dev.nedhuman.dupedetect.listener.BlockPlaceListener;
import dev.nedhuman.dupedetect.listener.InventoryClickListener;
import dev.nedhuman.dupedetect.listener.InventoryOpenListener;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class DupeDetect extends JavaPlugin {

    private static DupeDetect plugin;

    private Set<String> bypassPlayers;
    private Set<Material> detectItems;
    private String discordWebhookUrl;
    private boolean inventoryCheck;
    private boolean dontNotifyIfCreative;
    private boolean persistOverPlace;
    private boolean antiSpam;

    private List<DupeAlert> dupeAlerts;
    private Map<Long, Integer> alertPileup;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        // Load in the config stuff
        if(getConfig().getBoolean("discord-relay")) {
            discordWebhookUrl = getConfig().getString("discord-webhook");
        }
        bypassPlayers = new HashSet<>();
        bypassPlayers.addAll(getConfig().getStringList("bypass-players"));

        inventoryCheck = getConfig().getBoolean("on-inventory-open-check", true);
        boolean periodicalChecker = getConfig().getBoolean("periodical-checker", true);
        int checkerDelay = getConfig().getInt("checker-delay", 5);
        dontNotifyIfCreative = getConfig().getBoolean("dont-notify-if-creative");
        persistOverPlace = getConfig().getBoolean("persist-over-place", true);
        antiSpam = getConfig().getBoolean("anti-spam", true);

        detectItems = new HashSet<>();
        for(String i : getConfig().getStringList("detect-items")) {
            try {
                detectItems.add(Material.valueOf(i));
            }catch (IllegalArgumentException e) {
                getLogger().warning("Unrecognised detect item "+i+" found in config, ignoring");
            }
        }
        // Config stuff end

        dupeAlerts = new ArrayList<>();
        alertPileup = new HashMap<>();

        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryOpenListener(), this);
        getServer().getPluginManager().registerEvents(new BlockDropItemListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getCommand("dupedetect").setExecutor(new DupeDetectCommand());


        // Set up the periodical checker
        if(periodicalChecker) {
            new PeriodicalChecker().runTaskTimer(this, 20L * checkerDelay, 20L * checkerDelay);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean shouldIgnoreCreative() {
        return dontNotifyIfCreative;
    }

    public Set<String> getBypassPlayers() {
        return bypassPlayers;
    }

    public boolean detectThisItem(Material item) {
        return detectItems.contains(item);
    }

    public List<DupeAlert> getDupeAlerts() {
        return dupeAlerts;
    }

    public Map<Long, Integer> getAlertPileup() {
        return alertPileup;
    }

    public boolean isAntiSpam() {
        return antiSpam;
    }

    public DupeAlert findDupeAlert(int uuid) {
        for(DupeAlert i : dupeAlerts) {
            if(i.uuid() == uuid) {
                return i;
            }
        }

        return null;
    }

    public String getDiscordWebhookUrl() {
        return discordWebhookUrl;
    }

    public boolean shouldInventoryCheck() {
        return inventoryCheck;
    }

    public static DupeDetect getInstance() {
        return plugin;
    }

    public boolean isPersistOverPlace() {
        return persistOverPlace;
    }
}
