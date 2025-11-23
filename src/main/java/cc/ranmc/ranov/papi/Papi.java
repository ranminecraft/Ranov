package cc.ranmc.ranov.papi;


import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.game.Game;
import cc.ranmc.ranov.util.GameUtil;
import cc.ranmc.ranov.util.TeamUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        return "ranov";
    }

    @Override
    public @NotNull String getVersion() {
        return Main.getInstance().getDescription().getVersion();
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

        if (identifier.startsWith("teammate")) {
            // 获取队伍
            if (!TeamUtil.inTeam(player.getName())) return "";

            TeamUtil.Team team = TeamUtil.getTeam(player.getName());
            if (team == null) return "";

            // 获取队伍成员（排除自己）
            List<String> members = team.members.stream()
                    .filter(name -> !name.equals(player.getName()))
                    .collect(java.util.stream.Collectors.toList());

            // 变量格式：teammate1 / teammate2 / teammate3 ...
            String indexStr = identifier.replace("teammate", "");
            int idx;

            try {
                idx = Integer.parseInt(indexStr) - 1; // 变为下标
            } catch (NumberFormatException e) {
                return "";
            }

            // 不足数量返回空
            if (idx < 0 || idx >= members.size()) return "";

            return members.get(idx);
        }


        return "&c暂无";
    }
}