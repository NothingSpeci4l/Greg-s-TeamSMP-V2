package fr.gregwl.gregsteamsmp.commands.teamsub.map;

import fr.gregwl.gregsteamsmp.commands.SubCommand;
import fr.gregwl.gregsteamsmp.map.ScoreboardMapManager;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamScoreboard extends SubCommand {
    @Override
    public String getName() {
        return "scoreboard";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public void perform(Player player, String[] args) {
        ScoreboardMapManager.toggle(player);
    }

    @Override
    public List<String> getSubCommandArguments(Player player, String[] args) {
        return List.of();
    }
}
