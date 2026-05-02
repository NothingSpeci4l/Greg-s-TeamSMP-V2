package fr.gregwl.gregsteamsmp;

import fr.gregwl.gregsteamsmp.claims.ClaimEventHandler;
import fr.gregwl.gregsteamsmp.commands.TeamCommand;
import fr.gregwl.gregsteamsmp.files.*;
import fr.gregwl.gregsteamsmp.objects.Claim;
import fr.gregwl.gregsteamsmp.objects.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public final class GregsTeamSMP extends JavaPlugin{

    private static GregsTeamSMP instance;
    private TeamSerializationManager teamSerializationManager;
    private TeamOwnersSerializationManager teamOwnersSerializationManager;
    private PlayerSerializationManager playerSerializationManager;
    private ClaimSerializationManager claimSerializationManager;
    public static String msgPrefix = ("§f[§x§0§0§8§D§F§F§l§nG§x§0§F§8§2§F§F§l§nr§x§1§F§7§7§F§F§l§ne§x§2§E§6§C§F§F§l§ng§x§3§E§6§2§F§F§l§n'§x§4§D§5§7§F§F§l§ns §x§6§C§4§1§F§F§l§nT§x§7§B§3§6§F§F§l§ne§x§8§A§2§B§F§F§l§na§x§9§A§2§1§F§F§l§nm§x§A§9§1§6§F§F§l§nS§x§B§9§0§B§F§F§l§nM§x§C§8§0§0§F§F§l§nP§f] ");
    public static HashMap<String, String> invitedTeamPlayers = new HashMap<>();

    @Override
    public void onEnable() {


        invitedTeamPlayers.clear();
        instance = this;
        this.teamSerializationManager = new TeamSerializationManager();
        this.teamOwnersSerializationManager = new TeamOwnersSerializationManager();
        this.playerSerializationManager = new PlayerSerializationManager();
        this.claimSerializationManager = new ClaimSerializationManager();

        new File(GregsTeamSMP.getInstance().getDataFolder(), "/teams/").mkdirs();
        File saveDir = new File(GregsTeamSMP.getInstance().getDataFolder(), "/teams/");

        getCommand("team").setExecutor(new TeamCommand());
        Bukkit.getPluginManager().registerEvents(new ClaimEventHandler(), this);

        final File filePlayerList = new File(saveDir, "playerlist.json");
        if(!filePlayerList.exists()) {
            HashMap<String, String> hashmap = new HashMap<>();
            PlayerList playerList = new PlayerList(hashmap);

            final PlayerSerializationManager playerSerializationManager = GregsTeamSMP.getInstance().getPlayerSerializationManager();
            final String jsonplayer = playerSerializationManager.serialize(playerList);

            FileUtils.save(filePlayerList, jsonplayer);
        }

        final File fileClaimsList = new File(saveDir, "claims.json");
        if(!fileClaimsList.exists()) {
            HashMap<String, String> hashMap = new HashMap<>();
            Claim claim = new Claim(hashMap);

            final ClaimSerializationManager claimSerializationManager = GregsTeamSMP.getInstance().getClaimSerializationManager();
            final String jsonClaims = claimSerializationManager.serialize(claim);

            FileUtils.save(fileClaimsList, jsonClaims);
        }


    }



    @Override
    public void onDisable() {
        invitedTeamPlayers.clear();
    }

    public static GregsTeamSMP getInstance() {
        return instance;
    }

    public TeamSerializationManager getTeamSerializationManager() {
        return teamSerializationManager;
    }

    public TeamOwnersSerializationManager getTeamOwnersSerializationManager() {
        return teamOwnersSerializationManager;
    }

    public PlayerSerializationManager getPlayerSerializationManager() {
        return playerSerializationManager;
    }

    public ClaimSerializationManager getClaimSerializationManager() {
        return claimSerializationManager;
    }


}
