package gg.matthew.core.particle.armorstand;

import gg.matthew.Main;
import gg.matthew.core.particle.ParticleEffects;
import gg.matthew.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Circle {
    private static Circle instance;
    final double radius = 6.5;
    final double radPerSec = 1.5;
    final double radPerTick = radPerSec / 20f;

    public static synchronized Circle getInstance() {
        if (instance == null) instance = new Circle();
        return instance;
    }

    private Location getLocationAroundCircle(Location center, double angleInRadian) {
        double x = center.getX() + radius * Math.cos(angleInRadian);
        double z = center.getZ() + radius * Math.sin(angleInRadian);
        double y = center.getY();
        Location location = new Location(center.getWorld(), x, y, z);
        Vector difference = center.toVector().clone().subtract(location.toVector());
        location.setDirection(difference);
        return location;
    }

    private ArmorStand generateArmorStand(Location location) {
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setSmall(true);
        return stand;
    }

    public void winnerEffect(Player player, Particle.DustOptions dustOptions) {
        Location location = player.getEyeLocation();
        ArmorStand stand = generateArmorStand(location);
        var ref = new Object() {
            int tick = 0;
            boolean ended = false;

        };
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> ref.ended = true, 250);
        new BukkitRunnable() {
            @Override
            public void run() {
                Location circleLocation = getLocationAroundCircle(location, radPerTick * ref.tick);
                if (!ref.ended && Utils.isPlayerOnline(player.getName()) != null) {
                    ParticleEffects.getInstance().spawnWinnerEffect(circleLocation, dustOptions);
                    stand.setVelocity(new Vector(1, 0, 0));
                    stand.teleport(circleLocation);
                    ref.tick++;
                } else {
                    stand.remove();
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 2L);
    }
}
