package cc.ranmc.ranov.papi;


import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Papi extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ranica";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ransync";
    }

    @Override
    public @NotNull String getVersion() {
        return "Release";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {

        if (identifier.contains("author")) return "";

        if (player == null || !player.isOnline()) return "&c玩家不在线";



        return "&c暂无";
    }
}