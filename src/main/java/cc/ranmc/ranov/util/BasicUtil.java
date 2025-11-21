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
    public static String color(String text) {
        return text.replace("&","§");
    }

    /**
     * 后台信息
     */
    public static void print(String msg) {
        Bukkit.getConsoleSender().sendMessage(PREFIX + color(msg));
    }

    /**
     * 公屏信息
     */
    public static void say(String msg) {
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

    public static Location getLocation(World world, String locationStr) {
        if (world == null || locationStr == null || locationStr.isEmpty()) {
            BasicUtil.print("&c无法获取位置" + locationStr);
            return null;
        }
        String[] locationSplit = locationStr.split(",");
        if (locationSplit.length < 3) {
            print("&c无法获取位置 " + locationStr);
            return null;
        }
        Location location = new Location(world,
                Double.parseDouble(locationSplit[0]),
                Double.parseDouble(locationSplit[1]),
                Double.parseDouble(locationSplit[2]));
        if (locationSplit.length >= 5) {
            location.setYaw(Float.parseFloat(locationSplit[3]));
            location.setPitch(Float.parseFloat(locationSplit[4]));
        }
        return location;
    }

    public static Location getLocation(String locationStr) {
        if (locationStr == null || locationStr.isEmpty()) {
            BasicUtil.print("&c无法获取位置" + locationStr);
            return null;
        }
        String[] locationSplit = locationStr.split(",");
        if (locationSplit.length < 5) {
            print("&c无法获取位置 " + locationStr);
            return null;
        }
        World world = Bukkit.getWorld(locationSplit[0]);
        if (world == null) {
            BasicUtil.print("&c不存在世界" + locationSplit[0]);
            return null;
        }
        Location location = new Location(world,
                Double.parseDouble(locationSplit[1]),
                Double.parseDouble(locationSplit[2]),
                Double.parseDouble(locationSplit[3]));
        if (locationSplit.length == 6) {
            location.setYaw(Float.parseFloat(locationSplit[4]));
            location.setPitch(Float.parseFloat(locationSplit[5]));
        }
        return location;
    }

}
