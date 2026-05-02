package fr.gregwl.gregsteamsmp.map;

import fr.gregwl.gregsteamsmp.GregsTeamSMP;
import fr.gregwl.gregsteamsmp.files.TeamSerializationManager;
import fr.gregwl.gregsteamsmp.objects.Claim;
import fr.gregwl.gregsteamsmp.objects.Team;
import fr.gregwl.gregsteamsmp.files.ClaimSerializationManager;
import fr.gregwl.gregsteamsmp.files.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class MapManager {

    private static final int RADIUS = 7;
    public static final String BLOCK = "█";

    private static final ChatColor[] TEAM_COLORS = {
            ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.YELLOW,
            ChatColor.LIGHT_PURPLE, ChatColor.AQUA, ChatColor.GOLD, ChatColor.WHITE,
            ChatColor.DARK_RED, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN,
            ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE
    };

    private static final Map<String, ChatColor> colorCache = new HashMap<>();
    private static int colorIndex = 0;

    private static ChatColor getTeamColor(String teamName) {
        return colorCache.computeIfAbsent(teamName, k -> {
            ChatColor c = TEAM_COLORS[colorIndex % TEAM_COLORS.length];
            colorIndex++;
            return c;
        });
    }

    private static Claim loadClaims() {
        File saveDir = new File(GregsTeamSMP.getInstance().getDataFolder(), "teams/");
        File file = new File(saveDir, "claims.json");
        if (!file.exists()) return null;
        ClaimSerializationManager csm = GregsTeamSMP.getInstance().getClaimSerializationManager();
        String json = FileUtils.loadContent(file);
        if (json == null || json.isEmpty()) return null;
        return csm.deserialize(json);
    }

    private static Team loadTeam(String teamName) {
        File teamFile = new File(GregsTeamSMP.getInstance().getDataFolder(), "teams/" + teamName + ".json");
        if (!teamFile.exists()) return null;
        TeamSerializationManager tsm = GregsTeamSMP.getInstance().getTeamSerializationManager();
        String json = FileUtils.loadContent(teamFile);
        return tsm.deserialize(json);
    }

    public static List<String> buildMapLines(Player player) {
        Chunk center = player.getLocation().getChunk();
        int cx = center.getX();
        int cz = center.getZ();

        Claim claims = loadClaims();
        HashMap<String, String> chunks = (claims != null && claims.getChunks() != null)
                ? claims.getChunks()
                : new HashMap<>();

        List<String> lines = new ArrayList<>();

        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
            StringBuilder row = new StringBuilder();
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                if (dx == 0 && dz == 0) {
                    row.append(ChatColor.WHITE).append("✚");
                    continue;
                }
                String key = (cx + dx) + "," + (cz + dz);
                String teamName = chunks.get(key);

                if (teamName == null) {
                    row.append(ChatColor.DARK_GRAY).append(BLOCK);
                } else {
                    row.append(getTeamColor(teamName)).append(BLOCK);
                }
            }
            lines.add(row.toString());
        }
        return lines;
    }

    /**
     * Retourne les teams distinctes visibles dans le rayon, avec leur couleur.
     * Map : teamName -> ChatColor
     */
    public static Map<String, ChatColor> getVisibleTeamsColors(Player player) {
        Chunk center = player.getLocation().getChunk();
        int cx = center.getX();
        int cz = center.getZ();

        Claim claims = loadClaims();
        HashMap<String, String> chunks = (claims != null && claims.getChunks() != null)
                ? claims.getChunks()
                : new HashMap<>();

        Map<String, ChatColor> visible = new LinkedHashMap<>();

        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                String key = (cx + dx) + "," + (cz + dz);
                String teamName = chunks.get(key);
                if (teamName != null && !visible.containsKey(teamName)) {
                    visible.put(teamName, getTeamColor(teamName));
                }
            }
        }
        return visible;
    }
}