package nl.matsgemmeke.battlegrounds.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class UUIDDataTypeTest {

    private static final UUID RANDOM_UUID = UUID.fromString("f0fe1496-cdbe-44f1-ad94-fb5c986e3f00");
    private static final byte[] RANDOM_UUID_IN_BYTES = new byte[] {-16, -2, 20, -106, -51, -66, 68, -15, -83, -108, -5, 92, -104, 110, 63, 0};

    @Test
    public void convertUUIDToByteArrayPrimitive() {
        PersistentDataAdapterContext context = mock(PersistentDataAdapterContext.class);

        UUIDDataType dataType = new UUIDDataType();
        byte[] output = dataType.toPrimitive(RANDOM_UUID, context);

        assertArrayEquals(RANDOM_UUID_IN_BYTES, output);
    }

    @Test
    public void convertByteArrayPrimitiveToUUID() {
        PersistentDataAdapterContext context = mock(PersistentDataAdapterContext.class);

        UUIDDataType dataType = new UUIDDataType();
        UUID output = dataType.fromPrimitive(RANDOM_UUID_IN_BYTES, context);

        assertEquals(output, RANDOM_UUID);
    }
}
