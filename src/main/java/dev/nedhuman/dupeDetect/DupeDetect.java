package dev.nedhuman.dupeDetect;

import dev.nedhuman.dupeDetect.listener.InventoryClickListener;
import dev.nedhuman.dupeDetect.listener.InventoryOpenListener;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public final class DupeDetect extends JavaPlugin {

    private static DupeDetect plugin;

    private Set<String> bypassPlayers;
    private Set<Material> detectItems;
    private String discordWebhookUrl;
    private boolean inventoryCheck;
    private boolean dontNotifyIfCreative;

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

        detectItems = new HashSet<>();
        for(String i : getConfig().getStringList("detect-items")) {
            try {
                detectItems.add(Material.valueOf(i));
            }catch (IllegalArgumentException e) {
                getLogger().warning("Unrecognised detect item "+i+" found in config, ignoring");
            }
        }
        // Config stuff end

        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryOpenListener(), this);


        // Set up periodical checker
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

    public String getDiscordWebhookUrl() {
        return discordWebhookUrl;
    }

    public boolean shouldInventoryCheck() {
        return inventoryCheck;
    }

    public static DupeDetect getInstance() {
        return plugin;
    }
}
