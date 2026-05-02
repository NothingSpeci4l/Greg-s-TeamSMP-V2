package fr.gregwl.gregsteamsmp.map;

import fr.gregwl.gregsteamsmp.GregsTeamSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardMapManager {

    private static final Set<UUID> enabled = new HashSet<>();
    private static final Map<UUID, Integer> tasks = new HashMap<>();

    public static boolean isEnabled(Player p) {
        return enabled.contains(p.getUniqueId());
    }

    public static void toggle(Player p) {
        if (isEnabled(p)) {
            disable(p);
            p.sendMessage(GregsTeamSMP.msgPrefix + "The Claims Map is enabled !");
        } else {
            enable(p);
            p.sendMessage(GregsTeamSMP.msgPrefix + "The Claims Map is disabled !");
        }
    }

    public static void enable(Player p) {
        enabled.add(p.getUniqueId());
        int taskId = Bukkit.getScheduler().runTaskTimer(
                GregsTeamSMP.getInstance(),
                () -> {
                    if (p.isOnline() && isEnabled(p)) refresh(p);
                },
                0L, 20L
        ).getTaskId();
        tasks.put(p.getUniqueId(), taskId);
    }

    public static void disable(Player p) {
        enabled.remove(p.getUniqueId());
        Integer taskId = tasks.remove(p.getUniqueId());
        if (taskId != null) Bukkit.getScheduler().cancelTask(taskId);
        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public static void cleanup(Player p) {
        disable(p);
    }

    private static void refresh(Player p) {
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = sb.registerNewObjective(
                "teammap", "dummy",
                ChatColor.GOLD + "" + ChatColor.BOLD + "Claims " +
                        ChatColor.DARK_GRAY + "[" + ChatColor.GRAY +
                        p.getLocation().getChunk().getX() + ", " +
                        p.getLocation().getChunk().getZ() +
                        ChatColor.DARK_GRAY + "]"
        );
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> lines = MapManager.buildMapLines(p);

        int score = lines.size();
        Set<String> seen = new HashSet<>();

        for (String line : lines) {
            String entry = ensureUnique(line, seen);
            seen.add(entry);
            obj.getScore(entry).setScore(score--);
        }

        p.setScoreboard(sb);
    }

    private static String ensureUnique(String line, Set<String> seen) {
        String candidate = line;
        int i = 0;
        while (seen.contains(candidate)) {
            candidate = line + ChatColor.RESET.toString().repeat(++i);
        }
        return candidate;
    }
}