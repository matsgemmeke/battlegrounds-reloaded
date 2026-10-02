package nl.matsgemmeke.battlegrounds.game;

public class GameKey {

    private final String value;

    private GameKey(String value) {
        this.value = value;
    }

    public static GameKey ofArena(int id) {
        return new GameKey("ARENA-" + id);
    }

    public static GameKey ofFreeplay() {
        return new GameKey("FREEPLAY");
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
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
