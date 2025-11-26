package cc.ranmc.ranov.listener;

import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.game.Game;
import cc.ranmc.ranov.util.BasicUtil;
import cc.ranmc.ranov.util.GameUtil;
import cc.ranmc.ranov.util.TeamUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 玩家加入服务器传送到大厅
        Location location = BasicUtil.getLocation(Main.getInstance().getConfig().getString("lobby-location"));
        if (location != null) event.getPlayer().teleport(location);
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();

        if (!(inv instanceof DoubleChestInventory)) return;

        DoubleChestInventory dc = (DoubleChestInventory) inv;

        // 判断双箱是否完全空
        if (isEmpty(dc)) {
            DoubleChest holder = dc.getHolder();

            // 获取左、右两个 Chest 方块
            Chest left = (Chest) holder.getLeftSide();
            Chest right = (Chest) holder.getRightSide();

            if (left != null && left.getBlock() != null && left.getCustomName().endsWith(" 的死亡物品")) {
                left.getBlock().setType(Material.AIR);
            }
            if (right != null && right.getBlock() != null && right.getCustomName().endsWith(" 的死亡物品")) {
                right.getBlock().setType(Material.AIR);
            }
        }
    }

    private boolean isEmpty(DoubleChestInventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Game game = GameUtil.getGame(player);
        if (game == null) return;
        game.move(player, event.getTo());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TeamUtil.quit(player);
        Game game = GameUtil.getGame(player);
        if (game == null) return;
        game.quit(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        Game game = GameUtil.getGame(player);
        if (game == null) return;
        game.dead(player);
    }

}
