package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BuckshotSpreadPattern implements SpreadPattern {

    private float horizontalSpread;
    private float verticalSpread;
    private int pelletAmount;
    @NotNull
    private Random random;

    public BuckshotSpreadPattern(int pelletAmount, float horizontalSpread, float verticalSpread) {
        this.pelletAmount = pelletAmount;
        this.horizontalSpread = horizontalSpread;
        this.verticalSpread = verticalSpread;
        this.random = new Random();
    }

    @NotNull
    public Iterable<Location> getProjectileDirections(@NotNull Location shootingDirection) {
        List<Location> directions = new ArrayList<>();

        for (int i = 1; i <= pelletAmount; i++) {
            Location direction = shootingDirection.clone();

            float randomHorSpread = random.nextFloat(horizontalSpread);
            float randomVerSpread = random.nextFloat(verticalSpread);

            if (random.nextBoolean()) {
                randomHorSpread *= -1;
            }
            if (random.nextBoolean()) {
                randomVerSpread *= -1;
            }

            direction.setYaw(direction.getYaw() + randomHorSpread);
            direction.setPitch(direction.getPitch() + randomVerSpread);

            directions.add(direction);
        }

        return directions;
    }
}
