package cc.ranmc.ranov.command;

import cc.ranmc.ranov.util.TeamUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static cc.ranmc.ranov.Main.PREFIX;
import static cc.ranmc.ranov.util.BasicUtil.color;
import static cc.ranmc.ranov.util.BasicUtil.print;

public class TeamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             Command cmd,
                             @NotNull String label,
                             String[] args) {

        if (sender.hasPermission("ranov.admin") && args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(PREFIX + color(
                                "/team help 查看队伍帮助\n" +
                                "/team quit 离开/解散队伍\n" +
                                "/team transfer <玩家名> 转让队长\n" +
                                "/team accept <玩家名> 同意申请/邀请\n" +
                                "/team invite <玩家名> 邀请加入队伍\n" +
                                "/team join <玩家名> 申请加入队伍"));
                return true;
            }
        }

        if (!(sender instanceof Player)) {
            print("&c该指令不能在控制台输入");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 2) {
            if (!sender.hasPermission("ranov.user")) {
                player.sendMessage(PREFIX + color("&c没有权限"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(PREFIX + color("&c该玩家不在线"));
                return true;
            }
            if (args[0].equalsIgnoreCase("join")) {
                TeamUtil.invite(player, target);
                return true;
            }
            if (args[0].equalsIgnoreCase("invite")) {
                TeamUtil.join(player, target);
                return true;
            }
            if (args[0].equalsIgnoreCase("quit")) {
                TeamUtil.quit(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("accept")) {
                TeamUtil.accept(player, target);
                return true;
            }
            if (args[0].equalsIgnoreCase("transfer")) {
                TeamUtil.transfer(player, target);
                return true;
            }
        }

        sender.sendMessage(PREFIX + color("&c未知指令"));
        return true;
    }
}
