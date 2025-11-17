package cc.ranmc.ranov;

import cc.ranmc.ranov.game.Game;
import cc.ranmc.ranov.command.MainCommand;
import cc.ranmc.ranov.command.MainTabComplete;
import cc.ranmc.ranov.listener.PlayerListener;
import cc.ranmc.ranov.util.GameUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static cc.ranmc.ranov.util.BasicUtil.color;
import static cc.ranmc.ranov.util.BasicUtil.print;

public class Main extends JavaPlugin implements Listener {

    private static final String PLUGIN = "Ranov";
    public static final String PREFIX = color("&b" + PLUGIN + ">>>");
    @Getter
    private YamlConfiguration dataYml, langYml;
    @Getter
    private static Main instance;

    @Override
    public void onDisable() {
        for (Game game : new ArrayList<>(GameUtil.GAME_LIST)) {
            game.delete();
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        print("&e-----------------------");
        print("&b" + PLUGIN + " &dBy阿然");
        print("&b插件版本:"+getDescription().getVersion());
        print("&b服务器版本:"+getServer().getVersion());
        print("&chttps://www.ranmc.cc/");
        print("&e-----------------------");

        loadConfig();

        // 注册Event
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        // 注册指令
        Bukkit.getPluginCommand("ranov").setExecutor(new MainCommand());

        // 注册指令补全
        Bukkit.getPluginCommand("ranov").setTabCompleter(new MainTabComplete());

        super.onEnable();
    }

    /**
     * 加载配置文件
     */
    public void loadConfig() {
        saveDefaultConfig();
        reloadConfig();

        File dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) saveResource("data.yml", false);
        dataYml = YamlConfiguration.loadConfiguration(dataFile);

        File langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) saveResource("lang.yml", false);
        langYml = YamlConfiguration.loadConfiguration(langFile);

        /*if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            papi = new Papi();
            papi.register();
            print(PREFIX + color("&a成功加载PlaceholderAPI插件"));
        } else {
            print(PREFIX + color("&c无法找到PlaceholderAPI插件"));
        }*/
    }
}
