package cc.ranmc.ranov.papi;


import cc.ranmc.ranov.game.Game;
import cc.ranmc.ranov.util.GameUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Papi extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ranica";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ransync";
    }

    @Override
    public @NotNull String getVersion() {
        return "Release";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {

        if (identifier.contains("author")) return "";

        if (player == null || !player.isOnline()) return "&c玩家不在线";

        if (identifier.equals("time")) {
            Game game = GameUtil.getGame((Player) player);
            if (game == null || !game.isGaming() || game.getEndTime() == null) return "游戏未开始";
            long time = game.getEndTime() - System.currentTimeMillis();
            return new SimpleDateFormat("HH:mm:ss").format(new Date(time));
        }

        return "&c暂无";
    }
}