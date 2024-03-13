package space.xbossbar;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.xbossbar.util.Metrics;

public final class BossBarPlugin extends JavaPlugin implements Listener {

  private final BossBarManager manager = new BossBarManager(this);

  @Override
  public void onEnable() {
    saveDefaultConfig();

    new Metrics(this, 21328); // it doesn't affect productivity, lol :/

    manager.load();

    Bukkit.getPluginManager().registerEvents(this, this);
  }

  @Override
  public void onDisable() {
    manager.unload();
  }

  @Contract(pure = true)
  @Override
  public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String label, final @NotNull String @NotNull [] args) {
    reloadConfig();
    manager.index(0);
    manager.unload();
    manager.load();
    return true;
  }

  @EventHandler
  public void onJoin(final @NotNull PlayerJoinEvent event) {
    manager.bossBar().addPlayer(event.getPlayer());
  }

  @EventHandler
  public void onQuit(final @NotNull PlayerQuitEvent event) {
    manager.bossBar().removePlayer(event.getPlayer());
  }
}
