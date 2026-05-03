package fr.gregwl.gregsteamsmp.commands.teamsub.ad;

import fr.gregwl.gregsteamsmp.GregsTeamSMP;
import fr.gregwl.gregsteamsmp.commands.SubCommand;
import fr.gregwl.gregsteamsmp.files.*;
import fr.gregwl.gregsteamsmp.objects.Claim;
import fr.gregwl.gregsteamsmp.objects.PlayerList;
import fr.gregwl.gregsteamsmp.objects.Team;
import fr.gregwl.gregsteamsmp.objects.TeamOwners;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TeamAdmin extends SubCommand {

    private File saveDir = new File(GregsTeamSMP.getInstance().getDataFolder(), "/teams/");

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/team admin <delete|unclaim> <teamName>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.isOp() && !player.hasPermission("gregsteamsmp.admin")) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§fYou don't have permission.");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§fUsage: /team admin <delete|unclaim> <teamName>");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "delete" -> handleDelete(player, args);
            case "unclaim" -> handleUnclaim(player, args);
            default -> player.sendMessage(GregsTeamSMP.msgPrefix + "§fUsage: /team admin <delete|unclaim> <teamName>");
        }
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§fUsage: /team admin delete <teamName>");
            return;
        }

        String teamName = args[2];
        File teamFile = new File(saveDir, teamName + ".json");

        if (!teamFile.exists()) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§fTeam §l" + teamName + "§r§f not found.");
            return;
        }

        // Charger la team pour récupérer owner + membres
        TeamSerializationManager tsm = GregsTeamSMP.getInstance().getTeamSerializationManager();
        Team team = tsm.deserialize(FileUtils.loadContent(teamFile));

        // 1. Supprimer les membres + owner de playerlist.json
        File filePlayerList = new File(saveDir, "playerlist.json");
        PlayerSerializationManager psm = GregsTeamSMP.getInstance().getPlayerSerializationManager();
        PlayerList playerList = psm.deserialize(FileUtils.loadContent(filePlayerList));

        if (playerList != null && playerList.getPlayerList() != null) {
            // Supprimer l'owner
            playerList.getPlayerList().remove(team.getOwner());
            // Supprimer tous les membres
            for (String member : team.getMembers()) {
                playerList.getPlayerList().remove(member);
            }
            FileUtils.save(filePlayerList, psm.serialize(playerList));
        }

        File fileTeamOwners = new File(saveDir, "teamsowners.json");
        TeamOwnersSerializationManager tosm = GregsTeamSMP.getInstance().getTeamOwnersSerializationManager();
        TeamOwners teamOwners = tosm.deserialize(FileUtils.loadContent(fileTeamOwners));

        if(teamOwners != null && teamOwners.getTeamsOwners() != null) {
            teamOwners.getTeamsOwners().remove(team.getOwner());
        }

        // 2. Supprimer tous les claims de la team dans claims.json
        File fileClaimsList = new File(saveDir, "claims.json");
        ClaimSerializationManager csm = GregsTeamSMP.getInstance().getClaimSerializationManager();
        Claim claims = csm.deserialize(FileUtils.loadContent(fileClaimsList));

        if (claims != null && claims.getChunks() != null) {
            claims.getChunks().entrySet().removeIf(entry -> entry.getValue().equals(teamName));
            FileUtils.save(fileClaimsList, csm.serialize(claims));
        }

        // 3. Supprimer le fichier de la team
        teamFile.delete();

        player.sendMessage(GregsTeamSMP.msgPrefix + "§fTeam §l" + teamName + "§r§f deleted — members removed from playerlist, claims wiped.");
    }

    private void handleUnclaim(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§fUsage: /team admin unclaim <teamName>");
            return;
        }

        String teamName = args[2];
        File teamFile = new File(saveDir, teamName + ".json");

        if (!teamFile.exists()) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§fTeam §l" + teamName + "§r§f not found.");
            return;
        }

        File fileClaimsList = new File(saveDir, "claims.json");
        ClaimSerializationManager csm = GregsTeamSMP.getInstance().getClaimSerializationManager();
        Claim claims = csm.deserialize(FileUtils.loadContent(fileClaimsList));

        if (claims == null || claims.getChunks() == null) {
            player.sendMessage(GregsTeamSMP.msgPrefix + "§fNo claims found for team §l" + teamName + "§r§f.");
            return;
        }

        Chunk chunk = player.getLocation().getChunk();
        String chunkID = chunk.getX() + "," + chunk.getZ();
        claims.getChunks().remove(chunkID);

        FileUtils.save(fileClaimsList, csm.serialize(claims));

        player.sendMessage(GregsTeamSMP.msgPrefix + "§f§l 1§r§f claim removed from team §l" + teamName + "§r§f.");
    }

    @Override
    public List<String> getSubCommandArguments(Player player, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (!player.isOp() && !player.hasPermission("gregsteamsmp.admin")) return suggestions;

        if (args.length == 2) {
            for (String sub : List.of("delete", "unclaim")) {
                if (sub.startsWith(args[1].toLowerCase())) suggestions.add(sub);
            }
        } else if (args.length == 3) {
            File[] files = saveDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".json")
                            && !f.getName().equals("claims.json")
                            && !f.getName().equals("playerlist.json")) {
                        String name = f.getName().replace(".json", "");
                        if (name.toLowerCase().startsWith(args[2].toLowerCase())) {
                            suggestions.add(name);
                        }
                    }
                }
            }
        }

        return suggestions;
    }
}