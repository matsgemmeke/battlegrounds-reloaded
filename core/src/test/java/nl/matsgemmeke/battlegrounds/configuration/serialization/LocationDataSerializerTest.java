package nl.matsgemmeke.battlegrounds.configuration.serialization;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LocationDataSerializerTest {

    private static final String SECTION_ABSOLUTE_PATH = "example.path";
    private static final String WORLD = "world";
    private static final double X = 1.1;
    private static final double Y = 2.2;
    private static final double Z = 3.3;
    private static final float YAW = 90.0f;
    private static final float PITCH = 10.0f;

    private final LocationDataSerializer locationDataSerializer = new LocationDataSerializer();

    @Test
    @DisplayName("serialize sets values from given LocationData in given Section")
    void serialize() {
        LocationData locationData = new LocationData(WORLD, X, Y, Z, YAW, PITCH);
        Section section = mock(Section.class);

        locationDataSerializer.serialize(locationData, section);

        verify(section).set("world", WORLD);
        verify(section).set("x", X);
        verify(section).set("y", Y);
        verify(section).set("z", Z);
        verify(section).set("yaw", YAW);
        verify(section).set("pitch", PITCH);
    }

    @Test
    @DisplayName("deserialize throws SerializationException when world value is missing in given section")
    void deserialize_missingWorldValue() {
        Section section = mock(Section.class);
        when(section.getAbsolutePath()).thenReturn(SECTION_ABSOLUTE_PATH);
        when(section.getString("world")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationDataSerializer.deserialize(section))
                .isInstanceOf(SerializationException.class)
                .hasMessage("Missing value 'world' at example.path");
    }

    @Test
    @DisplayName("deserialize throws SerializationException when x value is missing in given section")
    void deserialize_missingXValue() {
        Section section = mock(Section.class);
        when(section.getAbsolutePath()).thenReturn(SECTION_ABSOLUTE_PATH);
        when(section.getString("world")).thenReturn(Optional.of(WORLD));
        when(section.getDouble("x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationDataSerializer.deserialize(section))
                .isInstanceOf(SerializationException.class)
                .hasMessage("Missing value 'x' at example.path");
    }

    @Test
    @DisplayName("deserialize throws SerializationException when y value is missing in given section")
    void deserialize_missingYValue() {
        Section section = mock(Section.class);
        when(section.getAbsolutePath()).thenReturn(SECTION_ABSOLUTE_PATH);
        when(section.getString("world")).thenReturn(Optional.of(WORLD));
        when(section.getDouble("x")).thenReturn(Optional.of(X));
        when(section.getDouble("y")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationDataSerializer.deserialize(section))
                .isInstanceOf(SerializationException.class)
                .hasMessage("Missing value 'y' at example.path");
    }

    @Test
    @DisplayName("deserialize throws SerializationException when z value is missing in given section")
    void deserialize_missingZValue() {
        Section section = mock(Section.class);
        when(section.getAbsolutePath()).thenReturn(SECTION_ABSOLUTE_PATH);
        when(section.getString("world")).thenReturn(Optional.of(WORLD));
        when(section.getDouble("x")).thenReturn(Optional.of(X));
        when(section.getDouble("y")).thenReturn(Optional.of(Y));
        when(section.getDouble("z")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationDataSerializer.deserialize(section))
                .isInstanceOf(SerializationException.class)
                .hasMessage("Missing value 'z' at example.path");
    }

    @Test
    @DisplayName("deserialize throws SerializationException when yaw value is missing in given section")
    void deserialize_missingYawValue() {
        Section section = mock(Section.class);
        when(section.getAbsolutePath()).thenReturn(SECTION_ABSOLUTE_PATH);
        when(section.getString("world")).thenReturn(Optional.of(WORLD));
        when(section.getDouble("x")).thenReturn(Optional.of(X));
        when(section.getDouble("y")).thenReturn(Optional.of(Y));
        when(section.getDouble("z")).thenReturn(Optional.of(Z));
        when(section.getDouble("yaw")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationDataSerializer.deserialize(section))
                .isInstanceOf(SerializationException.class)
                .hasMessage("Missing value 'yaw' at example.path");
    }

    @Test
    @DisplayName("deserialize throws SerializationException when pitch value is missing in given section")
    void deserialize_missingPitchValue() {
        Section section = mock(Section.class);
        when(section.getAbsolutePath()).thenReturn(SECTION_ABSOLUTE_PATH);
        when(section.getString("world")).thenReturn(Optional.of(WORLD));
        when(section.getDouble("x")).thenReturn(Optional.of(X));
        when(section.getDouble("y")).thenReturn(Optional.of(Y));
        when(section.getDouble("z")).thenReturn(Optional.of(Z));
        when(section.getDouble("yaw")).thenReturn(Optional.of((double) YAW));
        when(section.getDouble("pitch")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationDataSerializer.deserialize(section))
                .isInstanceOf(SerializationException.class)
                .hasMessage("Missing value 'pitch' at example.path");
    }

    @Test
    @DisplayName("deserialize returns LocationData with data from given section")
    void deserialize_successful() {
        Section section = mock(Section.class);
        when(section.getAbsolutePath()).thenReturn(SECTION_ABSOLUTE_PATH);
        when(section.getString("world")).thenReturn(Optional.of(WORLD));
        when(section.getDouble("x")).thenReturn(Optional.of(X));
        when(section.getDouble("y")).thenReturn(Optional.of(Y));
        when(section.getDouble("z")).thenReturn(Optional.of(Z));
        when(section.getDouble("yaw")).thenReturn(Optional.of((double) YAW));
        when(section.getDouble("pitch")).thenReturn(Optional.of((double) PITCH));

        LocationData locationData = locationDataSerializer.deserialize(section);

        assertThat(locationData.world()).isEqualTo(WORLD);
        assertThat(locationData.x()).isEqualTo(X);
        assertThat(locationData.y()).isEqualTo(Y);
        assertThat(locationData.z()).isEqualTo(Z);
        assertThat(locationData.yaw()).isEqualTo(YAW);
        assertThat(locationData.pitch()).isEqualTo(PITCH);
    }
}
