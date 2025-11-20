package cc.ranmc.ranov.util;

import cc.ranmc.ranov.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static cc.ranmc.ranov.Main.PREFIX;
import static cc.ranmc.ranov.util.BasicUtil.print;

public class WorldUtil {

    public static void deleteWorld(World world) {
        if (world == null) return;
        String name = world.getName();
        Location location = BasicUtil.getLocation(Main.getInstance().getConfig().getString("lobby-location"));
        new ArrayList<>(world.getPlayers()).forEach(player -> {
            if (player != null && player.isOnline()) player.teleport(location);
        });
        boolean unloaded = Bukkit.unloadWorld(world, false);
        if (!unloaded) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()-> deleteWorld(world), 10);
            return;
        }
        File folder = new File(Bukkit.getWorldContainer(), name);
        deleteFolder(folder);
    }

    private static void deleteFolder(File file) {
        if (!file.exists()) return;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteFolder(f);
                else f.delete();
            }
        }
        file.delete();
    }

    public static World copyWorldAndLoad(String name) {
        File src = new File(Main.getInstance().getDataFolder(), name);
        int i = 1;
        File dest = new File(Bukkit.getWorldContainer(), name + i);
        while (dest.exists()) {
            i++;
            dest = new File(Bukkit.getWorldContainer(), name + i);
        }
        copyFolder(src, dest);
        return new WorldCreator(name + i).createWorld();
    }

    private static void copyFolder(File src, File dest) {
        if (!dest.exists()) dest.mkdirs();
        File[] files = src.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.getName().equalsIgnoreCase("session.lock")) continue;
            if (file.getName().equalsIgnoreCase("uid.dat")) continue;
            File destFile = new File(dest, file.getName());
            if (file.isDirectory()) {
                copyFolder(file, destFile);
            } else try {
                Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                print(PREFIX + "复制文件失败 " + file.getName() + ": " + e.getMessage());
            }
        }
    }
}
