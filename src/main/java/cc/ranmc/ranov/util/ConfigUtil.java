package cc.ranmc.ranov.util;

import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.papi.Papi;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static cc.ranmc.ranov.Main.PREFIX;
import static cc.ranmc.ranov.util.BasicUtil.color;
import static cc.ranmc.ranov.util.BasicUtil.print;

public class ConfigUtil {
    private static final Main plugin = Main.getInstance();

    /**
     * 加载配置文件
     */
    public static void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        File dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) plugin.saveResource("data.yml", false);
        plugin.setDataYml(YamlConfiguration.loadConfiguration(dataFile));

        File langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!langFile.exists()) plugin.saveResource("lang.yml", false);
        plugin.setLangYml(YamlConfiguration.loadConfiguration(langFile));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Papi().register();
            print(PREFIX + color("&a成功加载PlaceholderAPI插件"));
        } else {
            print(PREFIX + color("&c无法找到PlaceholderAPI插件"));
        }
    }
}
