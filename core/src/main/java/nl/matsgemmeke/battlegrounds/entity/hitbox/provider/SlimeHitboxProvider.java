package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.entity.Slime;

import java.util.Set;
import java.util.stream.Collectors;

public class SlimeHitboxProvider implements HitboxProvider<Slime> {

    private final RelativeHitbox standingHitbox;

    public SlimeHitboxProvider(RelativeHitbox standingHitbox) {
        this.standingHitbox = standingHitbox;
    }

    @Override
    public Hitbox provideHitbox(Slime slime) {
        Location baseLocation = slime.getLocation();
        int size = slime.getSize();

        // No need to resize when size equals 1
        if (size == 1) {
            return new Hitbox(baseLocation, standingHitbox);
        }

        Set<HitboxComponent> sizedComponents = standingHitbox.components().stream()
                .map(component -> this.resizeComponent(component, size))
                .collect(Collectors.toSet());
        RelativeHitbox sizedHitbox = new RelativeHitbox(sizedComponents);

        return new Hitbox(baseLocation, sizedHitbox);
    }

    private HitboxComponent resizeComponent(HitboxComponent component, int size) {
        double height = component.height() * size;
        double width = component.width() * size;
        double depth = component.depth() * size;
        double offsetX = component.offsetX() * size;
        double offsetY = component.offsetY() * size;
        double offsetZ = component.offsetZ() * size;

        return new HitboxComponent(component.type(), height, width, depth, offsetX, offsetY, offsetZ);
    }
}
