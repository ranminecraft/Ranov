package cc.ranmc.ranov.listener;

import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.game.Game;
import cc.ranmc.ranov.util.BasicUtil;
import cc.ranmc.ranov.util.GameUtil;
import cc.ranmc.ranov.util.TeamUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 玩家加入服务器传送到大厅
        Location location = BasicUtil.getLocation(Main.getInstance().getConfig().getString("lobby-location"));
        if (location != null) event.getPlayer().teleport(location);
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
        game.quit(player);
    }

}
