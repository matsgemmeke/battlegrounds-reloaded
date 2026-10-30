package nl.matsgemmeke.battlegrounds.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDDataType implements PersistentDataType<byte[], UUID> {

    private static final UUIDDataType INSTANCE = new UUIDDataType();
    private static final int BYTE_BUFFER_SIZE = 16;

    private UUIDDataType() {
    }

    public static UUIDDataType getInstance() {
        return INSTANCE;
    }

    @Override
    @NotNull
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    @NotNull
    public Class<UUID> getComplexType() {
        return UUID.class;
    }

    @Override
    @NotNull
    public byte[] toPrimitive(@NotNull UUID complex, @NotNull PersistentDataAdapterContext context) {
        ByteBuffer buffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
        buffer.putLong(complex.getMostSignificantBits());
        buffer.putLong(complex.getLeastSignificantBits());

        return buffer.array();
    }

    @Override
    @NotNull
    public UUID fromPrimitive(@NotNull byte[] primitive, @NotNull PersistentDataAdapterContext context) {
        ByteBuffer buffer = ByteBuffer.wrap(primitive);
        long firstLong = buffer.getLong();
        long secondLong = buffer.getLong();

        return new UUID(firstLong, secondLong);
    }
}