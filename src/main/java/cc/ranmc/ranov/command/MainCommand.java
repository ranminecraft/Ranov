package cc.ranmc.ranov.command;

import cc.ranmc.ranov.Main;
import cc.ranmc.ranov.util.ConfigUtil;
import cc.ranmc.ranov.util.GameUtil;
import ink.ptms.adyeshach.core.Adyeshach;
import ink.ptms.adyeshach.core.entity.EntityInstance;
import ink.ptms.adyeshach.core.entity.EntityTypes;
import ink.ptms.adyeshach.core.entity.manager.Manager;
import ink.ptms.adyeshach.core.entity.manager.ManagerType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static cc.ranmc.ranov.Main.PREFIX;
import static cc.ranmc.ranov.util.BasicUtil.color;
import static cc.ranmc.ranov.util.BasicUtil.print;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             Command cmd,
                             @NotNull String label,
                             String[] args) {

        if (sender.hasPermission("ranov.admin") && args.length == 1) {
            /*if (args[0].equalsIgnoreCase("test")) {
                Manager manager = Adyeshach.INSTANCE.api().getPublicEntityManager(ManagerType.TEMPORARY);
                Player player = (Player) sender;
                EntityInstance npc = manager.create(EntityTypes.PLAYER, player.getLocation());
                npc.setCustomMeta("playername", "123");
                npc.updateEntityMetadata();
                return true;
            }*/
            if (args[0].equalsIgnoreCase("reload")){
                ConfigUtil.load();
                sender.sendMessage(PREFIX + color("&a重载成功"));
                return true;
            }
            if (args[0].equalsIgnoreCase("help")){
                sender.sendMessage(PREFIX + color(
                                "/ranov reload 重载插件\n" +
                                "/ranov help 查看帮助\n" +
                                "/ranov join 加入匹配队列"));
                return true;
            }
        }

        if (!(sender instanceof Player)) {
            print("&c该指令不能在控制台输入");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (!sender.hasPermission("ranov.user")) {
                player.sendMessage(PREFIX + color("&c没有权限"));
                return true;
            }
            if (args[0].equalsIgnoreCase("join")){
                GameUtil.getGame().join(player);
                return true;
            }
        }

        sender.sendMessage(PREFIX + color("&c未知指令"));
        return true;
    }
}
