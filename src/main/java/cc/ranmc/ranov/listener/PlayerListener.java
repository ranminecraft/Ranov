package cc.ranmc.ranov.listener;

import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.util.BasicUtil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 玩家加入服务器传送到大厅
        Location location = BasicUtil.getLocation(Main.getInstance().getConfig().getString("lobby-location"));
        if (location != null) event.getPlayer().teleport(location);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Main.getGame().quit(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Main.getGame().dead(event.getEntity().getPlayer());
    }


}
