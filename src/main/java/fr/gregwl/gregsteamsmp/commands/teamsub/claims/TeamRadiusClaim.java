package fr.gregwl.gregsteamsmp.commands.teamsub.claims;

import fr.gregwl.gregsteamsmp.GregsTeamSMP;
import fr.gregwl.gregsteamsmp.files.ClaimSerializationManager;
import fr.gregwl.gregsteamsmp.files.FileUtils;
import fr.gregwl.gregsteamsmp.files.PlayerSerializationManager;
import fr.gregwl.gregsteamsmp.objects.Claim;
import fr.gregwl.gregsteamsmp.objects.PlayerList;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
public class TeamRadiusClaim extends fr.gregwl.gregsteamsmp.commands.SubCommand {
    private static final int MAX_RADIUS = 6;

    private Plugin plugin = GregsTeamSMP.getInstance();
    private File saveDir = new File(plugin.getDataFolder(), "/teams/");

    @Override
    public String getName() {
        return "radiusclaim";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/team radiusclaim <rayon>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§fUsage : /team radiusclaim <radius> (max " + MAX_RADIUS + ")");
            return;
        }

        int radius;
        try {
            radius = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§fThe radius must be an integer.");
            return;
        }

        if (radius < 1 || radius > MAX_RADIUS) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§fThe radius must be between 1 and " + MAX_RADIUS + ".");
            return;
        }

        final File fileClaimsList = new File(saveDir, "claims.json");
        final ClaimSerializationManager claimSerializationManager = GregsTeamSMP.getInstance().getClaimSerializationManager();
        final String claimJsonExport = FileUtils.loadContent(fileClaimsList);
        final Claim claims = claimSerializationManager.deserialize(claimJsonExport);

        final File filePlayerList = new File(saveDir, "playerlist.json");
        final PlayerSerializationManager playerSerializationManager = GregsTeamSMP.getInstance().getPlayerSerializationManager();
        final String playerJsonExport = FileUtils.loadContent(filePlayerList);
        final PlayerList playerList = playerSerializationManager.deserialize(playerJsonExport);

        if (!playerList.getPlayerList().containsKey(player.getName())) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§fYou aren't in a team !");
            return;
        }

        String currentTeamName = playerList.getPlayerList().get(player.getName());
        Chunk center = player.getLocation().getChunk();

        int claimed = 0;
        int alreadyClaimed = 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                String chunkID = (center.getX() + dx) + "," + (center.getZ() + dz);

                if (claims.getChunks().containsKey(chunkID)) {
                    alreadyClaimed++;
                } else {
                    claims.getChunks().put(chunkID, currentTeamName);
                    claimed++;
                }
            }
        }

        final String claimJson = claimSerializationManager.serialize(claims);
        FileUtils.save(fileClaimsList, claimJson);

        int total = (radius * 2 + 1) * (radius * 2 + 1);
        player.sendMessage(GregsTeamSMP.msgPrefix + "§fClaimed §l" + claimed + "§r§f chunks out of " + total + ".");
        if (alreadyClaimed > 0) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§f" + alreadyClaimed + " chunk(s) already claimed, skipped.");
        }
    }

    @Override
    public List<String> getSubCommandArguments(Player player, String[] args) {
        return null;
    }
}
