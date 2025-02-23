package nl.matsgemmeke.battlegrounds.game;

import org.jetbrains.annotations.NotNull;

public class GameKey {

    @NotNull
    private final String value;

    private GameKey(@NotNull String value) {
        this.value = value;
    }

    @NotNull
    public static GameKey ofSession(int id) {
        return new GameKey("SESSION-" + id);
    }

    @NotNull
    public static GameKey ofTrainingMode() {
        return new GameKey("TRAINING-MODE");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        GameKey gameKey = (GameKey) obj;
        return value.equals(gameKey.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
