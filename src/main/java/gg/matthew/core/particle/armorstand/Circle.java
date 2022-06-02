package gg.matthew.core.particle.armorstand;

import gg.matthew.Main;
import gg.matthew.core.particle.ParticleEffects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Circle {
    private static Circle instance;

    public static synchronized Circle getInstance() {
        if (instance == null) instance = new Circle();
        return instance;
    }

    private Location getLocationAroundCircle(Location center, double radius, double angleInRadian) {
        double x = center.getX() + radius * Math.cos(angleInRadian);
        double z = center.getZ() + radius * Math.sin(angleInRadian);
        double y = center.getY();
        Location location = new Location(center.getWorld(), x, y, z);
        Vector difference = center.toVector().clone().subtract(location.toVector());
        location.setDirection(difference);
        return location;
    }

    //TODO handle quit event of the player (or just pass argument of the player and check if is online)
    //TODO also on the cancel don't forget to remove the armorstand
    public void spawnArmorStand(Location location) {
        double radius = 6.5;
        double radPerSec = 1.5;
        double radPerTick = radPerSec / 20f;
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setSmall(true);
        var ref = new Object() {
            int tick = 0;
            final Location firstLocation = getLocationAroundCircle(location, radius, radPerTick * tick);
            boolean firstTime = true;

        };
        Bukkit.getLogger().info(String.valueOf(ref.firstLocation)); //
        new BukkitRunnable() {
            @Override
            public void run() {
                Location circleLocation = getLocationAroundCircle(location, radius, radPerTick * ref.tick);
                if (circleLocation == ref.firstLocation) Bukkit.getLogger().info(String.valueOf(circleLocation)); //
                if (!ref.firstTime) if (ref.firstLocation != circleLocation) { //Comparing locations doesn't work!
                    ParticleEffects.getInstance().spawnWinnerEffect(circleLocation);
                    stand.setVelocity(new Vector(1, 0, 0));
                    stand.teleport(circleLocation);
                    ref.tick++;
                } else {
                    cancel();
                }
                if (ref.firstTime) ref.firstTime = !ref.firstTime;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 2L);
    }
}
