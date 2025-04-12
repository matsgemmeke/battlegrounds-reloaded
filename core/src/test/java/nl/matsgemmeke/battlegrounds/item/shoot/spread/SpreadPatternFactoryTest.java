package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.SpreadPatternSpecification;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class SpreadPatternFactoryTest {

    @Test
    public void shouldMakeBuckshotSpreadPatternInstance() {
        SpreadPatternSpecification specification = new SpreadPatternSpecification("BUCKSHOT", 1, 0.5f, 0.5f);

        SpreadPatternFactory factory = new SpreadPatternFactory();
        SpreadPattern result = factory.create(specification);

        assertInstanceOf(BuckshotSpreadPattern.class, result);
    }
}
