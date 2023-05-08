package primalcat.hesiovanila;

import org.bukkit.Location;
import org.bukkit.World;

public class Utilities {
    public Location findSafeLocation(World world, int x, int y, int z, int radius) {
        Location location = new Location(world, x, y, z);
        for (int i = 0; i < 10; i++) {
            double angle = Math.random() * Math.PI * 2;
            double distance = Math.random() * radius;
            int dx = (int) Math.round(Math.sin(angle) * distance);
            int dz = (int) Math.round(Math.cos(angle) * distance);
            int newY = world.getHighestBlockYAt(x + dx, z + dz);
            location.setX(x + dx);
            location.setY(newY);
            location.setZ(z + dz);
            if (location.getBlock().getType().isAir() &&
                    location.clone().subtract(0, 1, 0).getBlock().getType().isSolid()) {
                return location;
            }
        }
        return null;
    }
}
