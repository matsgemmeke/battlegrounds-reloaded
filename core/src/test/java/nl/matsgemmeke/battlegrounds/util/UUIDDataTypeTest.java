package nl.matsgemmeke.battlegrounds.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UUIDDataTypeTest {

    private static final UUID RANDOM_UUID = UUID.fromString("f0fe1496-cdbe-44f1-ad94-fb5c986e3f00");
    private static final byte[] RANDOM_UUID_IN_BYTES = new byte[] {-16, -2, 20, -106, -51, -66, 68, -15, -83, -108, -5, 92, -104, 110, 63, 0};

    @Test
    @DisplayName("toPrimitive returns byte array from given UUID")
    void toPrimitive() {
        PersistentDataAdapterContext context = mock(PersistentDataAdapterContext.class);

        UUIDDataType dataType = UUIDDataType.getInstance();
        byte[] output = dataType.toPrimitive(RANDOM_UUID, context);

        assertThat(output).isEqualTo(RANDOM_UUID_IN_BYTES);
    }

    @Test
    @DisplayName("fromPrimitive returns UUID from given byte array")
    void fromPrimitive() {
        PersistentDataAdapterContext context = mock(PersistentDataAdapterContext.class);

        UUIDDataType dataType = UUIDDataType.getInstance();
        UUID output = dataType.fromPrimitive(RANDOM_UUID_IN_BYTES, context);

        assertThat(output).isEqualTo(RANDOM_UUID);
    }
}
