package gg.matthew.core.particle;

import gg.matthew.core.utils.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Particle {
    private static Particle instance;
    int arrayLocation = 0;
    private double[][] coordinates;

    public static synchronized Particle getInstance() {
        if (instance == null) instance = new Particle();
        return instance;
    }

    public void spawnWinnerEffect(Player player) {
        Location location = player.getEyeLocation();
        org.bukkit.Particle.DustOptions dustOptions = new org.bukkit.Particle.DustOptions(Color.fromRGB(0, 127, 255), 1.0F);
        for (int index = 0; index <= arrayLocation; index++) {
            double[] coords = new double[]{};
            for (double coordinate : coordinates[index]) {
                coords = Utils.addElement(coords, coordinate);
            }
            location.add(coords[0], coords[1], coords[2]);
            location.getWorld().spawnParticle(org.bukkit.Particle.REDSTONE, location, 0, dustOptions);
            location.subtract(coords[0], coords[1], coords[2]);
        }
    }

    public void cacheWinnerEffect() {
        coordinates = new double[11 * 20][];
        int levels = 10;
        for (double index = 0; index <= Math.PI; index += Math.PI / levels) {
            double radius = Math.sin(index);
            double y = Math.cos(index);
            for (double a = 0; a < Math.PI * 2; a += Math.PI / levels) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                coordinates[arrayLocation++] = new double[]{x, y, z};
            }
        }
    }
}