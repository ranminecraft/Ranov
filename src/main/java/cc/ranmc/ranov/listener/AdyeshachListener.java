package cc.ranmc.ranov.listener;

import cc.ranmc.ranov.game.Game;
import cc.ranmc.ranov.util.GameUtil;
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent;
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AdyeshachListener implements Listener {

    @EventHandler
    public void onAdyeshachEntityInteractEvent(AdyeshachEntityInteractEvent event) {
        String uuid = event.getEntity().getUniqueId();
        for (Game game : GameUtil.GAME_LIST) {
            if (game.getCmdMap().containsKey(uuid)) {
                event.getPlayer().chat("/" + game.getCmdMap().get(uuid));
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
