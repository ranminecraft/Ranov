package cc.ranmc.ranov.listener;

import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.util.BasicUtil;
import cc.ranmc.ranov.util.GameUtil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 玩家加入服务器传送到大厅
        Location location = BasicUtil.getLocation(Main.getInstance().getConfig().getString("lobby-location"));
        if (location != null) event.getPlayer().teleport(location);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        new ArrayList<>(GameUtil.GAME_LIST).forEach(game ->
                game.quit(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        new ArrayList<>(GameUtil.GAME_LIST).forEach(game ->
                game.dead(event.getEntity().getPlayer()));
    }

}
