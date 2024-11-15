package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpreadPatternFactoryTest {

    @Test
    public void shouldMakeBuckshotSpreadPatternInstance() {
        Section section = mock(Section.class);
        when(section.getString("type")).thenReturn("BUCKSHOT");

        SpreadPatternFactory factory = new SpreadPatternFactory();
        SpreadPattern result = factory.make(section);

        assertInstanceOf(BuckshotSpreadPattern.class, result);
    }

    @Test
    public void shouldThrowErrorWhenSectionHasInvalidType() {
        Section section = mock(Section.class);
        when(section.getString("type")).thenReturn("fail");

        SpreadPatternFactory factory = new SpreadPatternFactory();

        assertThrows(IllegalArgumentException.class, () -> factory.make(section));
    }
}
