package cc.ranmc.ranov.game;

import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.bean.Area;
import cc.ranmc.ranov.util.BasicUtil;
import cc.ranmc.ranov.util.GameUtil;
import cc.ranmc.ranov.util.WorldUtil;
import ink.ptms.adyeshach.core.Adyeshach;
import ink.ptms.adyeshach.core.entity.EntityInstance;
import ink.ptms.adyeshach.core.entity.EntityTypes;
import ink.ptms.adyeshach.core.entity.manager.Manager;
import ink.ptms.adyeshach.core.entity.manager.ManagerType;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
    private List<Area> leavaLocationList = new ArrayList<>();
    private Map<String,String> leavingMap = new HashMap<>();

    public void join(Player player) {
        if (player == null || isGameing(player)) return;
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
            player.setHealth(player.getMaxHealth());
            player.setHealth(20);
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

        // 加载撤离点
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (String line : plugin.getConfig().getStringList("leave-location")) {
                String[] lineSplit = line.split(" ");
                if (lineSplit.length != 2) {
                    print("&c撤离点配置错误" + line);
                    continue;
                }
                Location start = getLocation(warWorld.getName() + "," + lineSplit[0]);
                Location end = getLocation(warWorld.getName() + "," + lineSplit[1]);
                if (start == null || end == null) {
                    print("&c撤离点配置错误" + line);
                    continue;
                }
                leavaLocationList.add(new Area(start, end));
            }
            for (String playerName : playList) {
                Player target = Bukkit.getPlayer(playerName);
                if (target == null) continue;
                target.sendMessage("撤离点已经开启");
            }
        }, plugin.getConfig().getInt("leave-start-time", 120) * 20L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            createNpc();
            createMob();
            createChest();

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

    private void createChest() {
        for (String line : plugin.getConfig().getStringList("chest-location")) {
            String[] chestInfo = line.split(" ");
            if (chestInfo.length < 2) {
                print("&cCHEST配置错误 " + line);
                continue;
            }
            Location location = getLocation(warWorld.getName() + "," + chestInfo[0]);
            Block block = location.getBlock();
            block.setType(Material.CHEST);
            Chest chest = (Chest) block.getState();
            chest.setCustomName(chestInfo[1]);
            Inventory inv = chest.getInventory().getHolder().getInventory();
            getChestItem(chestInfo[1]).forEach(inv::addItem);
            chest.update();
        }
    }

    private List<ItemStack> getChestItem(String name) {
        List<ItemStack> list = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("chest." + name)) {
            try {
                String[] itemInfo = line.split(" ");
                if (Double.parseDouble(itemInfo[1]) < Math.random()) continue;
                ItemStack item = MythicMobs.inst().getItemManager().getItemStack(itemInfo[0]);
                String[] parts = itemInfo[2].split("~");
                int min = Integer.parseInt(parts[0].trim());
                int max = Integer.parseInt(parts[1].trim());
                item.setAmount((int) ((Math.random() * (max - min + 1)) + min));
                list.add(item);
            } catch (Exception e) {
                print("&cCHEST配置错误 " + line + " 错误原因" + e.getMessage());
            }
        }
        return list;
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
        deadBox(player);
        checkGameOver();
    }

    public void quit(Player player) {
        if (!isGameing(player)) return;
        playList.remove(player.getName());
        deadBox(player);
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

    public void move(Player player, Location location) {
        if (!isGameing(player)) return;
        if (leavingMap.containsKey(player.getName())) return;
        for (Area area : leavaLocationList) {
            if (area.inArea(location)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    leavingMap.remove(player.getName());
                    if (!leavaLocationList.contains(area)) return;
                    leavaLocationList.remove(area);
                    if (area.inArea(player.getLocation())) leave(player);
                }, plugin.getConfig().getInt("leave-wait-time", 3) * 20L);
            }
        }
    }

    public void deadBox(Player player) {
        Location loc = player.getLocation();
        Block block = loc.getBlock();

        block.setType(Material.CHEST);
        Block block2 = block.getRelative(BlockFace.EAST);
        block2.setType(Material.CHEST);

        Chest chest1 = (Chest) block.getState();
        Chest chest2 = (Chest) block2.getState();
        chest1.setCustomName(player.getName() + " 的死亡物品");
        chest2.setCustomName(player.getName() + " 的死亡物品");
        chest1.update();
        chest2.update();

        Inventory inv = chest1.getInventory().getHolder().getInventory();
        inv.setContents(player.getInventory().getContents());
        player.getInventory().clear();
    }

}
