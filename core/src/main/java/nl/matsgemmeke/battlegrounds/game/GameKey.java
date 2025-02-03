package nl.matsgemmeke.battlegrounds.game;

import org.jetbrains.annotations.NotNull;

public class GameKey {

    @NotNull
    private final String value;

    private GameKey(@NotNull String value) {
        this.value = value;
    }

    public static GameKey ofSession(int id) {
        return new GameKey("SESSION-" + id);
    }

    public static GameKey ofTrainingMode() {
        return new GameKey("TRAINING-MODE");
    }

    public String toString() {
        return value;
    }
}
