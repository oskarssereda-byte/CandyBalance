package lol.candysmp.balance;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CandyBalance extends JavaPlugin implements Listener {
    private Economy economy;
    private BukkitTask task;
    private final Map<UUID, TextDisplay> displays = new HashMap<>();

    @Override
    public void onEnable() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().severe("No Vault economy provider found. Install Vault and EssentialsX Economy (or another Vault economy provider).");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        economy = rsp.getProvider();
        Bukkit.getPluginManager().registerEvents(this, this);
        for (Player p : Bukkit.getOnlinePlayers()) updateDisplay(p);
        task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) updateDisplay(p);
        }, 20L, 20L);
        getLogger().info("CandyBalance enabled.");
    }

    @Override
    public void onDisable() {
        if (task != null) task.cancel();
        for (TextDisplay display : displays.values()) {
            if (display != null && display.isValid()) display.remove();
        }
        displays.clear();
    }

    @EventHandler public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(this, () -> updateDisplay(e.getPlayer()), 10L);
    }
    @EventHandler public void onQuit(PlayerQuitEvent e) { removeDisplay(e.getPlayer()); }
    @EventHandler public void onWorldChange(PlayerChangedWorldEvent e) {
        removeDisplay(e.getPlayer());
        updateDisplay(e.getPlayer());
    }
    @EventHandler public void onRespawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTaskLater(this, () -> updateDisplay(e.getPlayer()), 2L);
    }
    @EventHandler public void onTeleport(PlayerTeleportEvent e) {
        if (e.getTo() != null && e.getFrom().getWorld() != e.getTo().getWorld()) {
            Bukkit.getScheduler().runTaskLater(this, () -> updateDisplay(e.getPlayer()), 2L);
        }
    }

    private void updateDisplay(Player player) {
        if (!player.isOnline() || !player.isValid()) return;
        TextDisplay display = displays.get(player.getUniqueId());
        if (display == null || !display.isValid() || display.getWorld() != player.getWorld()) {
            if (display != null && display.isValid()) display.remove();
            Location spawnAt = player.getLocation().clone().add(0, 2.15, 0);
            display = player.getWorld().spawn(spawnAt, TextDisplay.class, d -> {
                d.setBillboard(Display.Billboard.CENTER);
                d.setAlignment(TextDisplay.TextAlignment.CENTER);
                d.setSeeThrough(false);
                d.setShadowed(true);
                d.setDefaultBackground(false);
                d.setPersistent(false);
                d.setInvulnerable(true);
                d.setGravity(false);
            });
            displays.put(player.getUniqueId(), display);
        }
        display.teleport(player.getLocation().clone().add(0, 2.15, 0));
        display.setText(ChatColor.LIGHT_PURPLE + "$ " + ChatColor.WHITE + format(economy.getBalance(player)));
    }

    private String format(double amount) {
        double abs = Math.abs(amount);
        if (abs >= 1_000_000_000) return compact(amount / 1_000_000_000d, "B");
        if (abs >= 1_000_000) return compact(amount / 1_000_000d, "M");
        if (abs >= 1_000) return compact(amount / 1_000d, "K");
        return new DecimalFormat("#,##0.##").format(amount);
    }

    private String compact(double value, String suffix) {
        return new DecimalFormat("0.#").format(value) + suffix;
    }

    private void removeDisplay(Player player) {
        TextDisplay display = displays.remove(player.getUniqueId());
        if (display != null && display.isValid()) display.remove();
    }
}
