package cc.ranmc.ranov.util;

import cc.ranmc.ranov.Main;

import static cc.ranmc.ranov.util.BasicUtil.color;

public class LangUtil {

    public static String getLang(String key) {
        return color(Main.getInstance().getLangYml().getString(key, "语言文件缺失" + key));
    }
}
