package nl.matsgemmeke.battlegrounds.storage;

public class DatabaseConfiguration {

    private static final String SQLITE_CONNECTION_URL = "jdbc:sqlite:plugins/Battlegrounds/battlegrounds.db";

    public String getSqliteConnectionUrl() {
        return SQLITE_CONNECTION_URL;
    }
}
