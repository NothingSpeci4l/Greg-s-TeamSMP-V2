package fr.gregwl.gregsteamsmp.map;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.Map;

public class SendMap2Chat {

    public static void sendMapToChat(Player player) {
        Chunk center = player.getLocation().getChunk();

        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "┌──────── " +
                ChatColor.BOLD + "Claims Map" +
                ChatColor.GOLD + " ────────┐");
        player.sendMessage(ChatColor.GRAY + "  Chunk : " +
                ChatColor.WHITE + center.getX() + ", " + center.getZ());
        player.sendMessage("");

        for (String line : MapManager.buildMapLines(player)) {
            player.sendMessage("  " + line);
        }

        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "  Legend :");
        player.sendMessage("  " + ChatColor.WHITE + "✚ " +
                ChatColor.GRAY + "Your position");
        player.sendMessage("  " + ChatColor.DARK_GRAY + MapManager.BLOCK + " " +
                ChatColor.GRAY + "Free chunks");

        Map<String, ChatColor> visible = MapManager.getVisibleTeamsColors(player);
        for (Map.Entry<String, ChatColor> entry : visible.entrySet()) {
            player.sendMessage("  " + entry.getValue() + MapManager.BLOCK + " " +
                    ChatColor.GRAY + entry.getKey());
        }

        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "└─────────────────────────────┘");
        player.sendMessage("");
    }
}
