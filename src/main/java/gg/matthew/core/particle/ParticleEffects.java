package gg.matthew.core.particle;

import gg.matthew.core.utils.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import java.util.Vector;

public class ParticleEffects {
    //TODO implement particle effects for a player which only has one live and one for runners which didn't kill the dragon when they won and one lose particle
    private static ParticleEffects instance;
    Vector<double[]> sphereCoordinates = new Vector<>();
    Vector<double[]> hexagonCoordinates = new Vector<>();

    public static synchronized ParticleEffects getInstance() {
        if (instance == null) instance = new ParticleEffects();
        return instance;
    }

    public void spawnWinnerEffect(Location location, Particle.DustOptions dustOptions) {
        runSphereEffect(location, dustOptions);
        runHexagonEffect(location);
    }

    public void cacheEffects() {
        sphereEffect();
        hexagonEffect();
    }

    private void sphereEffect() {
        int levels = 8;
        for (double index = 0; index <= Math.PI; index += Math.PI / levels) {
            double radius = Math.sin(index);
            double y = Math.cos(index);
            for (double a = 0; a < Math.PI * 2; a += Math.PI / levels) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                double[] coords = new double[]{x, y, z};
                sphereCoordinates.add(coords);
            }
        }
    }

    private void runSphereEffect(Location location, Particle.DustOptions dustOptions) {
        for (double[] array : sphereCoordinates) {
            double[] coords = new double[]{};
            for (double coord : array) {
                coords = Utils.addElement(coords, coord);
            }
            location.add(coords[0], coords[1], coords[2]);
            location.getWorld().spawnParticle(Particle.REDSTONE, location, 0, dustOptions);
            location.subtract(coords[0], coords[1], coords[2]);
        }
    }

    private void hexagonEffect() {
        int points = 6;
        for (int iteration = 0; iteration < points; iteration++) {
            double angle = 360.0 / points * iteration;
            angle = Math.toRadians(angle);
            double x = Math.cos(angle);
            double z = Math.sin(angle);
            double[] coords = new double[]{x, z};
            hexagonCoordinates.add(coords);
        }
    }

    private void runHexagonEffect(Location location) {
        for (double[] array : hexagonCoordinates) {
            double[] coords = new double[]{};
            for (double coord : array) {
                coords = Utils.addElement(coords, coord);
            }
            location.add(coords[0], 0, coords[1]);
            location.getWorld().spawnParticle(Particle.GLOW, location, 0, 0, 0, 0, 0);
            location.subtract(coords[0], 0, coords[1]);
        }
    }
}