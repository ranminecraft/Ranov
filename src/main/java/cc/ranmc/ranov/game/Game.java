package cc.ranmc.ranov.game;

import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.util.BasicUtil;
import cc.ranmc.ranov.util.GameUtil;
import cc.ranmc.ranov.util.WorldUtil;
import ink.ptms.adyeshach.core.Adyeshach;
import ink.ptms.adyeshach.core.AdyeshachEntityControllerRegistry;
import ink.ptms.adyeshach.core.entity.EntityInstance;
import ink.ptms.adyeshach.core.entity.EntityTypes;
import ink.ptms.adyeshach.core.entity.controller.ControllerGenerator;
import ink.ptms.adyeshach.core.entity.manager.Manager;
import ink.ptms.adyeshach.core.entity.manager.ManagerType;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static cc.ranmc.ranov.util.BasicUtil.color;
import static cc.ranmc.ranov.util.BasicUtil.getLocation;
import static cc.ranmc.ranov.util.BasicUtil.print;
import static cc.ranmc.ranov.util.LangUtil.getLang;

@Data
public class Game {

    private final Main plugin = Main.getInstance();
    private boolean gaming = false;
    private List<String> playList = new ArrayList<>();
    private World warWorld, waitWorld;
    private Long endTime = null;
    private Map<String,String> cmdMap = new HashMap<>();

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
            Location location = BasicUtil.getLocation(plugin.getConfig().getString("lobby-location"));
            for (String playerName : playList) {
                Player player = Bukkit.getPlayer(playerName);
                if (player == null) continue;
                player.sendMessage(getLang("wait-cancel"));
                player.teleport(location);
            }
            GameUtil.GAME_LIST.remove(this);
            return;
        }
        // 游戏开始
        endTime = System.currentTimeMillis() +
                (Main.getInstance().getConfig().getInt("timeout", 10) * 60 * 1000L);
        List<String> locationList = plugin.getConfig().getStringList("spawn-location");
        warWorld = WorldUtil.copyWorldAndLoad(plugin.getConfig().getString("war-world"));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            createNpc();
            createMob();

            for (String playerName : playList) {
                if (locationList.isEmpty()) {
                    print("&c致命错误，出生位置配置数量不够");
                    break;
                }
                Player player = Bukkit.getPlayer(playerName);
                if (player == null) continue;
                int randomLocation = new Random().nextInt(locationList.size());
                Location location = BasicUtil.getLocation(warWorld.getName() + "," +
                        locationList.get(randomLocation));
                locationList.remove(randomLocation);
                player.teleport(location);
                player.sendMessage(getLang("game-on"));
            }
        }, 20);
    }

    private void createMob() {
        for (String line : plugin.getConfig().getStringList("spawn-mob")) {
            String[] npcInfo = line.split(" ");
            if (npcInfo.length < 2) {
                print("&cMOB配置错误 " + line);
                continue;
            }
            Location location = getLocation(warWorld.getName() + "," + npcInfo[1]);
            if (location == null) {
                print("&cMOB位置配置错误 " + line);
                continue;
            }
            MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(npcInfo[0]);
            if (mob == null) {
                print("&cMOB不存在 " + line);
            } else {
                mob.spawn(BukkitAdapter.adapt(location), 1);
            }
            //Entity entity = knight.getEntity().getBukkitEntity();
        }
    }

    private void createNpc() {
        Manager manager = Adyeshach.INSTANCE.api().getPublicEntityManager(ManagerType.PERSISTENT);
        for (String line : plugin.getConfig().getStringList("spawn-npc")) {
            String[] npcInfo = line.split(" ");
            if (npcInfo.length < 4) {
                print("&cNPC配置错误 " + line);
                continue;
            }
            Location location = getLocation(warWorld.getName() + "," + npcInfo[2]);
            if (location == null) {
                print("&cNPC位置配置错误 " + line);
                continue;
            }
            try {
                EntityInstance npc = manager.create(EntityTypes.valueOf(npcInfo[0]), location);
                npc.setCustomName(color(npcInfo[1]));
                npc.setCustomMeta("playername", color(npcInfo[1]));
                npc.updateEntityMetadata();
                StringBuilder builder = new StringBuilder();
                for (int i = 3; i < npcInfo.length; i++) {
                    builder.append(npcInfo[i]).append(" ");
                }
                if (builder.length() > 0) builder.deleteCharAt(builder.length() - 1);
                cmdMap.put(npc.getUniqueId(), builder.toString());
            } catch (NullPointerException ignored) {
                print("&cNPC位置配置错误 " + line);
            }
        }
    }

    public void checkGameOver() {
        if (!playList.isEmpty()) return;
        delete();
    }

    public void delete() {
        Manager manager = Adyeshach.INSTANCE.api().getPublicEntityManager(ManagerType.TEMPORARY);
        new ArrayList<>(manager.getEntities()).forEach(npc -> {
            if (warWorld == npc.getWorld()) npc.remove();
        });
        WorldUtil.deleteWorld(warWorld);
        WorldUtil.deleteWorld(waitWorld);
        GameUtil.GAME_LIST.remove(this);
    }

    public void checkTimeout() {
        if (gaming && endTime != null && endTime < System.currentTimeMillis()) {
            Location location = BasicUtil.getLocation(plugin.getConfig().getString("lobby-location"));
            new ArrayList<>(playList).forEach(playerName -> {
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {
                    player.sendMessage(getLang("timeout"));
                    playList.remove(player.getName());
                    player.teleport(location);
                }
            });
            delete();
        }
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
