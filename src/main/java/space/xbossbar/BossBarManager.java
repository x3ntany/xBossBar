package space.xbossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitTask;
import space.xbossbar.util.Color;

public final class BossBarManager {

  private final BossBarPlugin plugin;
  private BossBar bossBar;
  private BukkitTask task;
  private int index = 0;

  public BossBarManager(BossBarPlugin plugin) {
    this.plugin = plugin;
  }

  public void load() {
    final var section = plugin.getConfig().getConfigurationSection("bossbars");

    if (section == null) {
      return;
    }

    final var keys = section.getKeys(false).stream().toList();

    if (keys.isEmpty()) {
      return;
    }

    if (task != null && !task.isCancelled()) {
      task.cancel();
    }

    final var sectionConfig = section.getConfigurationSection(keys.get(index));

    if (sectionConfig == null) {
      return;
    }

    final var title = Color.parse(sectionConfig.getString("title", ""));
    final var color = BarColor.valueOf(sectionConfig.getString("color", "WHITE").toUpperCase());
    final var style = BarStyle.valueOf(sectionConfig.getString("style", "SOLID").toUpperCase());

    if (bossBar == null) {
      bossBar = Bukkit.createBossBar(title, color, style);
      Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
    } else {
      bossBar.setTitle(title);
      bossBar.setColor(color);
      bossBar.setStyle(style);
    }

    final var delay = Double.parseDouble(sectionConfig.getString("delay", "10.0"));
    var steps = Double.parseDouble(sectionConfig.getString("steps", String.valueOf(delay)));

    final var increasing = steps < 0;

    steps = Math.abs(steps);

    final var increment = 1.0 / steps;
    final var progress = new double[]{increasing ? 0.0 : 1.0};

    bossBar.setProgress(progress[0]);

    final var period = (long) (delay * 20.0 / steps);

    task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
      progress[0] += increasing ? increment : -increment;

      bossBar.setProgress(Math.min(Math.max(progress[0], 0.0), 1.0));

      if (progress[0] >= 1.0 || progress[0] <= 0) {
        index = (index + 1) % keys.size();
        load();
      }
    }, period, period);
  }

  public void unload() {
    if (bossBar != null) {
      bossBar.removeAll();
      bossBar = null;
    }

    if (task != null) {
      task.cancel();
      task = null;
    }
  }

  public BossBar bossBar() {
    return bossBar;
  }

  public void index(final int index) {
    this.index = index;
  }
}
