package cc.ranmc.ranov.bean;

import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.util.BasicUtil;
import cc.ranmc.ranov.util.WorldUtil;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
    private World warWorld, waitWorld;

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
        waitWorld = WorldUtil.copyWorldAndLoad(plugin.getConfig().getString("wait-world"));
        Location location = BasicUtil.getLocation(waitWorld.getName() + "," +
                plugin.getConfig().getString("wait-location"));

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
        for (int i = time; i > 0; i--) {
            int sec = i;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (String playerName : playList) {
                    Player player = Bukkit.getPlayer(playerName);
                    if (player == null) continue;
                    player.sendMessage(getLang("wait")
                            .replace("%sec%", String.valueOf(sec)));
                }
                if (sec == 1) start();
            }, (time - i) * 20L);
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
        if (locationList.size() < playList.size()) {
            print(PREFIX + "&c致命错误，出生位置配置数量不够");
            return;
        }
        warWorld = WorldUtil.copyWorldAndLoad(plugin.getConfig().getString("war-world"));
        for (String playerName : playList) {
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) continue;
            int randomLocation = new Random().nextInt(locationList.size());
            Location location = BasicUtil.getLocation(warWorld.getName() + "," +
                    locationList.get(randomLocation));
            locationList.remove(randomLocation);
            player.teleport(location);
            player.sendMessage(getLang("game-on"));
        }
    }

    public void checkGameOver() {
        if (!playList.isEmpty()) return;
        WorldUtil.deleteWorld(warWorld);
        WorldUtil.deleteWorld(waitWorld);
    }

    public void dead(Player player) {
        if (!isGameing(player)) return;
        player.sendMessage(getLang("dead"));
        playList.remove(player.getName());
        checkGameOver();
    }

    public void quit(Player player) {
        if (!isGameing(player)) return;
        playList.remove(player.getName());
        checkGameOver();
    }

    public void leave(Player player) {
        if (!isGameing(player)) return;
        playList.remove(player.getName());
        player.sendMessage(getLang("leave"));
        checkGameOver();
    }

    public boolean isGameing(Player player) {
        return gaming && playList.contains(player.getName());
    }

}
