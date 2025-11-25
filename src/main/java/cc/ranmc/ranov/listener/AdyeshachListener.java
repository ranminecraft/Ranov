package cc.ranmc.ranov.listener;

import cc.ranmc.ranov.game.Game;
import cc.ranmc.ranov.util.GameUtil;
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent;
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class AdyeshachListener implements Listener {

    private Map<String,Long> cdMap = new HashMap<>();

    @EventHandler
    public void onAdyeshachEntityInteractEvent(AdyeshachEntityInteractEvent event) {
        String uuid = event.getEntity().getUniqueId();
        Player player = event.getPlayer();
        Game game = GameUtil.getGame(player);
        if (game == null) return;
        long now = System.currentTimeMillis();
        if (game.getCmdMap().containsKey(uuid)) {
            if (cdMap.getOrDefault(player.getName(), 0L) + 100 > now) return;
            event.getPlayer().chat("/" + game.getCmdMap().get(uuid));
            cdMap.put(player.getName(), now);
        }
    }

    @EventHandler
    public void onAdyeshachEntityDamageEvent(AdyeshachEntityDamageEvent event) {
        Player player = event.getPlayer();
        Game game = GameUtil.getGame(player);
        if (game == null) return;
        String uuid = event.getEntity().getUniqueId();
        if (game.getCmdMap().containsKey(uuid)) {
            event.getPlayer().chat("/" + game.getCmdMap().get(uuid));
        }
    }

}
