package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.SpreadPatternSpec;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class SpreadPatternFactoryTest {

    @Test
    public void createReturnsBuckshotSpreadPatternInstanceWhenTypeEqualsToBuckshot() {
        SpreadPatternSpec spec = new SpreadPatternSpec();
        spec.type = "BUCKSHOT";
        spec.projectileAmount = 1;
        spec.horizontalSpread = 0.5f;
        spec.verticalSpread = 0.5f;

        SpreadPatternFactory factory = new SpreadPatternFactory();
        SpreadPattern spreadPattern = factory.create(spec);

        assertInstanceOf(BuckshotSpreadPattern.class, spreadPattern);
    }

    @Test
    public void createThrowsSpreadPatternCreationExceptionWhenRequiredVariablesAreMissing() {
        SpreadPatternSpec spec = new SpreadPatternSpec();
        spec.type = "BUCKSHOT";

        SpreadPatternFactory factory = new SpreadPatternFactory();

        assertThatThrownBy(() -> factory.create(spec))
                .isInstanceOf(SpreadPatternCreationException.class)
                .hasMessage("Cannot create spread pattern with type BUCKSHOT because of invalid spec: Required 'projectileAmount' value is missing");
    }

    @Test
    public void createReturnsSingleProjectileSpreadPatternInstanceWhenTypeEqualsToSingleProjectile() {
        SpreadPatternSpec spec = new SpreadPatternSpec();
        spec.type = "SINGLE_PROJECTILE";

        SpreadPatternFactory factory = new SpreadPatternFactory();
        SpreadPattern spreadPattern = factory.create(spec);

        assertInstanceOf(SingleProjectileSpreadPattern.class, spreadPattern);
    }
}
