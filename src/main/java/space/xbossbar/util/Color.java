package space.xbossbar.util;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public final class Color {

  public static @NotNull String parse(final String input) {
    String text = Pattern.compile("#[a-fA-F0-9]{6}")
            .matcher(input)
            .replaceAll(match -> ChatColor.of(match.group()).toString());
    return ChatColor.translateAlternateColorCodes('&', text);
  }
}
