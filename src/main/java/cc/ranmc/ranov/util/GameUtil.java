package cc.ranmc.ranov.util;

import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameUtil {

    public static List<Game> GAME_LIST = new ArrayList<>();

    public static void join(Player player) {
        TeamUtil.Team team = TeamUtil.getTeam(player.getName());
        Game game = null;
        for (Game g : GAME_LIST) {
            if (!g.isGaming() && (Main.getInstance().getConfig().getInt("player", 2) -
                    GAME_LIST.get(GAME_LIST.size() - 1).getPlayList().size()) >=
                    (team == null ? 1 : team.members.size())) {
                game = g;
                break;
            }
        }
        if (game == null) {
            game = new Game();
            GAME_LIST.add(game);
        }
        if (team != null) {
            Game finalGame = game;
            team.members.forEach(member ->
                    finalGame.join(Bukkit.getPlayer(member)));
        } else {
            game.join(player);
        }
    }

    public static Game getGame(Player player) {
        for (Game game : GAME_LIST) {
            if (game.getPlayList().contains(player.getName())) {
                return game;
            }
        }
        return null;
    }

}
