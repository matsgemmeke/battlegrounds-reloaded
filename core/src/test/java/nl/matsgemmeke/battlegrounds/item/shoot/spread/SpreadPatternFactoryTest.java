package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpreadPatternFactoryTest {

    @Test
    public void shouldMakeBuckshotSpreadPatternInstance() {
        Section section = mock(Section.class);
        when(section.getString("type")).thenReturn("BUCKSHOT");

        SpreadPatternFactory factory = new SpreadPatternFactory();
        SpreadPattern result = factory.make(section);

        assertNotNull(result);
        assertThat(result, instanceOf(BuckshotSpreadPattern.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowErrorWhenSectionHasInvalidType() {
        Section section = mock(Section.class);
        when(section.getString("type")).thenReturn("fail");

        SpreadPatternFactory factory = new SpreadPatternFactory();
        factory.make(section);
    }
}
