package nl.matsgemmeke.battlegrounds.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UUIDGeneratorTest {

    @Test
    public void createRandomUUID() {
        UUIDGenerator generator = new UUIDGenerator();
        UUID first = generator.generateRandom();
        UUID second = generator.generateRandom();

        assertNotEquals(first, second);
    }
}
