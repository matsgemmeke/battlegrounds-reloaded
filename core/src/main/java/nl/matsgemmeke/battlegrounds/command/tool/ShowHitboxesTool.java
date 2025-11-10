package nl.matsgemmeke.battlegrounds.command.tool;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.resolver.HitboxResolver;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Map;

public class ShowHitboxesTool {

    private static final long SCHEDULE_DELAY = 0L;
    private static final long SCHEDULE_INTERVAL = 1L;
    private static final Map<HitboxComponentType, Color> COMPONENT_TYPE_COLORS = Map.of(
            HitboxComponentType.HEAD, Color.RED,
            HitboxComponentType.TORSO, Color.YELLOW,
            HitboxComponentType.LIMBS, Color.BLUE
    );
    private static final int[][] EDGES = {
            { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 },
            { 4, 5 }, { 5, 6 }, { 6, 7 }, { 7, 4 },
            { 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 }
    };
    private static final double DISTANCE_BETWEEN_PARTICLES = 0.2;

    private final HitboxResolver hitboxResolver;
    private final Scheduler scheduler;
    private final Translator translator;

    @Inject
    public ShowHitboxesTool(HitboxResolver hitboxResolver, Scheduler scheduler, Translator translator) {
        this.hitboxResolver = hitboxResolver;
        this.scheduler = scheduler;
        this.translator = translator;
    }

    public void execute(Player player, int seconds, double range) {
        long duration = (long) seconds * 20;

        Schedule schedule = scheduler.createRepeatingSchedule(SCHEDULE_DELAY, SCHEDULE_INTERVAL, duration);
        schedule.addTask(() -> this.displayNearbyEntityHitboxes(player, range));
        schedule.start();

        Map<String, Object> values = Map.of("bg_range", range, "bg_seconds", seconds);
        String message = translator.translate(TranslationKey.TOOL_HITBOX_SUCCESSFUL.getPath()).replace(values);

        player.sendMessage(message);
    }

    private void displayNearbyEntityHitboxes(Player player, double range) {
        Location playerLocation = player.getLocation();
        World world = player.getWorld();

        for (Entity entity : world.getNearbyEntities(playerLocation, range, range, range)) {
            Hitbox hitbox = hitboxResolver.resolveHitbox(entity).orElse(null);

            if (hitbox == null) {
                continue;
            }

            PositionHitbox positionHitbox = hitbox.getCurrentPositionHitbox();

            for (HitboxComponent component : positionHitbox.components()) {
                Location baseLocation = entity.getLocation();
                // Add component offsets
                baseLocation.add(component.offsetX(), component.offsetY(), component.offsetZ());
                // Add half the height to get the center location of the box
                baseLocation.add(0, component.height() / 2, 0);

                this.displayHitboxComponentOutline(world, baseLocation, component, entity.getLocation().getYaw());
            }
        }
    }

    private void displayHitboxComponentOutline(World world, Location location, HitboxComponent component, float yaw) {
        Vector[] corners = this.getCornerVectors(component);
        double radians = Math.toRadians(yaw);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        // todo
        Vector offsetVector = new Vector(component.offsetX(), component.offsetY(), component.offsetZ());

        for (Vector corner : corners) {
            double x = corner.getX();
            double z = corner.getZ();

            corner.setX(x * cos - z * sin);
            corner.setZ(x * sin + z * cos);
        }

        for (int[] edge : EDGES) {
            Vector start = corners[edge[0]];
            Vector end = corners[edge[1]];

            Vector edgeVector = end.clone().subtract(start);
            double length = edgeVector.length();
            edgeVector.normalize();

            for (double d = 0; d <= length; d += DISTANCE_BETWEEN_PARTICLES) {
                Vector point = start.clone().add(edgeVector.clone().multiply(d));
                Location particleLocation = location.clone().add(point);
                Color color = COMPONENT_TYPE_COLORS.get(component.type());

                world.spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0,0, 0, new DustOptions(color, 1.0f));
            }
        }
    }

    private Vector[] getCornerVectors(HitboxComponent component) {
        double w = component.width() / 2;
        double h = component.height() / 2;
        double d = component.depth() / 2;

        // All 8 corners (x, y, z)
        return new Vector[] {
                new Vector(-w, -h, -d),
                new Vector(w, -h, -d),
                new Vector(w, -h, d),
                new Vector(-w, -h, d),
                new Vector(-w, h, -d),
                new Vector(w, h, -d),
                new Vector(w, h, d),
                new Vector(-w, h, d)
        };
    }
}
