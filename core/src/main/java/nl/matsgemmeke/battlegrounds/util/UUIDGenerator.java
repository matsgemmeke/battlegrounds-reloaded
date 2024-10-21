package nl.matsgemmeke.battlegrounds.util;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UUIDGenerator {

    @NotNull
    public UUID generateRandom() {
        return UUID.randomUUID();
    }
}
