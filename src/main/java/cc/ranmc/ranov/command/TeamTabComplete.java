package cc.ranmc.ranov.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      String[] args) {
        if (args.length == 1) return Arrays.asList("invtie", "help", "join", "quit", "accept", "transfer");
        if (args.length == 2 && Arrays.asList("invtie", "join","accept", "transfer").contains(args[0])) return null;
        return new ArrayList<>();
    }

}
