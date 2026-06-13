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

    public static GameKey parse(String value) {
        if (value.equals("OPEN-MODE")) {
            return ofOpenMode();
        }

        if (value.startsWith("SESSION-")) {
            String id = value.substring("SESSION-".length());

            try {
                return ofSession(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid SESSION id: " + id);
            }
        }

        throw new IllegalArgumentException("Unknown GameKey format: " + value);
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
