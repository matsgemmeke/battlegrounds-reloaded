package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import nl.matsgemmeke.battlegrounds.configuration.item.shoot.spread.BuckshotSpreadPatternSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.spread.SingleProjectileSpreadPatternSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class SpreadPatternFactoryTest {

    private final SpreadPatternFactory factory = new SpreadPatternFactory();

    @Test
    @DisplayName("create returns BuckshotSpreadPattern instance")
    void create_buckshotSpreadPattern() {
        BuckshotSpreadPatternSpec spec = new BuckshotSpreadPatternSpec();
        spec.type = "BUCKSHOT";
        spec.projectileAmount = 1;
        spec.horizontalSpread = 0.5f;
        spec.verticalSpread = 0.5f;

        SpreadPattern spreadPattern = factory.create(spec);

        assertInstanceOf(BuckshotSpreadPattern.class, spreadPattern);
    }

    @Test
    @DisplayName("create returns SingleProjectileSpreadPattern instance")
    void create_singleProjectileSpreadPattern() {
        SingleProjectileSpreadPatternSpec spec = new SingleProjectileSpreadPatternSpec();
        spec.type = "SINGLE_PROJECTILE";

        SpreadPattern spreadPattern = factory.create(spec);

        assertInstanceOf(SingleProjectileSpreadPattern.class, spreadPattern);
    }
}
