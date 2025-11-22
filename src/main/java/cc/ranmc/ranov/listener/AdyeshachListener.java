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
        long now = System.currentTimeMillis();
        for (Game game : GameUtil.GAME_LIST) {
            if (game.getCmdMap().containsKey(uuid)) {
                if (cdMap.getOrDefault(player.getName(), 0L) + 100 > now) break;
                event.getPlayer().chat("/" + game.getCmdMap().get(uuid));
                cdMap.put(player.getName(), now);
                break;
            }
        }
    }

    @EventHandler
    public void onAdyeshachEntityDamageEvent(AdyeshachEntityDamageEvent event) {
        String uuid = event.getEntity().getUniqueId();
        for (Game game : GameUtil.GAME_LIST) {
            if (game.getCmdMap().containsKey(uuid)) {
                event.getPlayer().chat("/" + game.getCmdMap().get(uuid));
                break;
            }
        }
    }

}
