package nl.matsgemmeke.battlegrounds.game;

public class GameKey {

    private final String value;

    private GameKey(String value) {
        this.value = value;
    }

    public static GameKey ofOpenMode() {
        return new GameKey("OPEN-MODE");
    }

    public static GameKey ofSession(int id) {
        return new GameKey("SESSION-" + id);
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
