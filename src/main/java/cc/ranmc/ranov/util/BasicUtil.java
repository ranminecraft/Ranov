package cc.ranmc.ranov.util;

import cc.ranmc.ranov.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import static cc.ranmc.ranov.Main.PREFIX;

public class BasicUtil {

    /**
     * 执行指令
     */
    public static void run(String command) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    /**
     * 文本颜色
     */
    public static String color(String text){
        return text.replace("&","§");
    }

    /**
     * 后台信息
     */
    public static void print(String msg){
        Bukkit.getConsoleSender().sendMessage(color(msg));
    }

    /**
     * 公屏信息
     */
    public static void say(String msg){
        Bukkit.broadcastMessage(color(msg));
    }

    public static String getLocation(Location location) {
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }

    public static Location getLocation(String locationStr) {
        if (locationStr == null || locationStr.isEmpty()) {
            BasicUtil.print(PREFIX + color("&c无法获取位置") + locationStr);
            return null;
        }
        String[] date = locationStr.split(",");
        World world = Bukkit.getWorld(date[0]);
        if (world == null) {
            BasicUtil.print(PREFIX + color("&c不存在世界") + date[0]);
            return null;
        }
        Location location = new Location(world,
                Double.parseDouble(date[1]),
                Double.parseDouble(date[2]),
                Double.parseDouble(date[3]));
        if (date.length == 6) {
            location.setYaw(Float.parseFloat(date[4]));
            location.setPitch(Float.parseFloat(date[5]));
        }
        return location;
    }

}
