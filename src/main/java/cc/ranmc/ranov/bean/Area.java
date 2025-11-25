package cc.ranmc.ranov.bean;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Data
public class Area {
    private Location start, end;

    public Area(Location start, Location end) {
        this.start = start;
        this.end = end;
    }

    public boolean inArea(Player player) {
        return inArea(player.getLocation());
    }

    public boolean inArea(Location loc) {
        double x1 = Math.min(start.getX(), end.getX());
        double x2 = Math.max(start.getX(), end.getX());
        double y1 = Math.min(start.getY(), end.getY());
        double y2 = Math.max(start.getY(), end.getY());
        double z1 = Math.min(start.getZ(), end.getZ());
        double z2 = Math.max(start.getZ(), end.getZ());

        double px = loc.getX();
        double py = loc.getY();
        double pz = loc.getZ();

        return px >= x1 && px <= x2 &&
                py >= y1 && py <= y2 &&
                pz >= z1 && pz <= z2;
    }
}
