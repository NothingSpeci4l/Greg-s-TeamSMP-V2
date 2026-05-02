package fr.gregwl.gregsteamsmp.commands.teamsub.map;

import fr.gregwl.gregsteamsmp.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.List;

import static fr.gregwl.gregsteamsmp.map.SendMap2Chat.sendMapToChat;

public class TeamMap extends SubCommand {
    @Override
    public String getName() {
        return "map";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {
        sendMapToChat(player);
    }

    @Override
    public List<String> getSubCommandArguments(Player player, String[] args) {
        return List.of();
    }
}
