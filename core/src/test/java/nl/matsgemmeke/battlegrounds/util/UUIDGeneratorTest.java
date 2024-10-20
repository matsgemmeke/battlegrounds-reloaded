package nl.matsgemmeke.battlegrounds.util;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNotEquals;

public class UUIDGeneratorTest {

    @Test
    public void createRandomUUID() {
        UUIDGenerator generator = new UUIDGenerator();
        UUID first = generator.generateRandom();
        UUID second = generator.generateRandom();

        assertNotEquals(first, second);
    }
}
