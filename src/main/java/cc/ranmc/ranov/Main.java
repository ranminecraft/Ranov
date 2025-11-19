package cc.ranmc.ranov;

import cc.ranmc.ranov.game.Game;
import cc.ranmc.ranov.command.MainCommand;
import cc.ranmc.ranov.command.MainTabComplete;
import cc.ranmc.ranov.listener.PlayerListener;
import cc.ranmc.ranov.util.ConfigUtil;
import cc.ranmc.ranov.util.GameUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static cc.ranmc.ranov.util.BasicUtil.color;
import static cc.ranmc.ranov.util.BasicUtil.print;

public class Main extends JavaPlugin implements Listener {

    private static final String PLUGIN_NAME = "Ranov";
    public static final String PREFIX = color("&b" + PLUGIN_NAME + ">>>");
    @Getter
    @Setter
    private YamlConfiguration dataYml, langYml;
    @Getter
    private static Main instance;

    @Override
    public void onDisable() {
        for (Game game : new ArrayList<>(GameUtil.GAME_LIST)) {
            game.delete();
        }
        super.onDisable();
    }

    @Override
    public void onEnable() {
        instance = this;
        print("&e-----------------------");
        print("&b" + PLUGIN_NAME + " &dBy阿然");
        print("&b插件版本 " + getDescription().getVersion());
        print("&b服务器版本 " + getServer().getVersion());
        print("&chttps://www.ranmc.cc/");
        print("&e-----------------------");

        /*HttpUtil.get("https://api.ranmc.cc/auth", result -> {
            if () {

            }
        });*/

        // 加载配置文件
        ConfigUtil.load();

        // 注册Event
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        // 注册指令
        Bukkit.getPluginCommand("ranov").setExecutor(new MainCommand());

        // 注册指令补全
        Bukkit.getPluginCommand("ranov").setTabCompleter(new MainTabComplete());

        // 检查超出撤离时间
        Bukkit.getScheduler().runTaskTimer(this, ()->
                GameUtil.GAME_LIST.forEach(Game::checkTimeout), 20, 20);

        super.onEnable();
    }
}
