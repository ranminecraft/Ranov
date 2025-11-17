package cc.ranmc.ranov.bean;

import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.util.BasicUtil;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static cc.ranmc.ranov.Main.PREFIX;
import static cc.ranmc.ranov.util.BasicUtil.print;
import static cc.ranmc.ranov.util.LangUtil.getLang;

@Data
public class Game {

    private final Main plugin = Main.getInstance();
    private boolean gaming = false;
    private List<String> playList = new ArrayList<>();

    public void join(Player player) {
        if (isGameing(player)) return;
        if (gaming) {
            player.sendMessage(getLang("join-fail"));
            return;
        }
        if (playList.contains(player.getName())) {
            playList.remove(player.getName());
            player.sendMessage(getLang("join-cancel"));
            return;
        }
        playList.add(player.getName());
        player.sendMessage(getLang("join"));
        ready();
    }

    public void ready() {
        if (playList.size() < plugin.getConfig().getInt("player", 2)) {
            return;
        }
        // 匹配成功
        gaming = true;
        Location location = BasicUtil.getLocation(plugin.getConfig().getString("wait-location"));
        for (String playerName : playList) {
            Player player = Bukkit.getPlayer(playerName);
            player.sendMessage(getLang("join-ok"));
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.setSaturation(20);
            if (location != null) player.teleport(location);
        }
        // 倒计时
        int time = plugin.getConfig().getInt("wait-time", 10);
        while (time > 0) {
            time--;
            int finalTime = time;
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                for (String playerName : playList) {
                    Player player = Bukkit.getPlayer(playerName);
                    if (player == null) continue;
                    player.sendMessage(getLang("wait")
                            .replace("%sec%", String.valueOf(finalTime)));
                }
                if (finalTime == 0) start();
            }, time * 20L);
        }
    }

    public void start() {
        if (playList.size() < plugin.getConfig().getInt("player", 2)) {
            // 人数不足，取消游戏
            gaming = false;
            Location location = BasicUtil.getLocation(plugin.getConfig().getString("lobby-location"));
            for (String playerName : playList) {
                Player player = Bukkit.getPlayer(playerName);
                if (player == null) continue;
                player.sendMessage(getLang("wait-cancel"));
                player.teleport(location);
            }
            playList.clear();
            return;
        }
        // 游戏开始
        List<String> locationList = plugin.getConfig().getStringList("spawn-location");
        if (locationList.size() >= playList.size()) {
            print(PREFIX + "&c致命错误，出生位置配置数量不够");
            return;
        }
        for (String playerName : playList) {
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) continue;
            int randomLocation = new Random().nextInt(locationList.size());
            Location location = BasicUtil.getLocation(locationList.get(randomLocation));
            locationList.remove(randomLocation);
            player.teleport(location);
            player.sendMessage(getLang("game-on"));
        }
    }

    public void dead(Player player) {

    }

    public void quit(Player player) {

    }

    public void leave(Player player) {

    }

    public boolean isGameing(Player player) {
        return gaming && playList.contains(player.getName());
    }

}
