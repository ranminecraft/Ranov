package cc.ranmc.ranov.util;

import cc.ranmc.ranov.game.Game;

import java.util.ArrayList;
import java.util.List;

public class GameUtil {

    public static List<Game> GAME_LIST = new ArrayList<>();

    public static Game getGame() {
        if (GAME_LIST.isEmpty()) {
            GAME_LIST.add(new Game());
            return GAME_LIST.get(0);
        }
        if (GAME_LIST.get(GAME_LIST.size() - 1).isGaming()) {
            GAME_LIST.add(new Game());
        }
        return GAME_LIST.get(GAME_LIST.size() - 1);
    }

}
