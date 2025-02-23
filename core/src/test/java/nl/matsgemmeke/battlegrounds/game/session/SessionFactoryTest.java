package nl.matsgemmeke.battlegrounds.game.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SessionFactoryTest {

    @TempDir
    private File setupFolder;

    @Test
    public void createMakesNewSessionInstanceAndCreatesConfigFile() {
        int id = 1;
        SessionConfiguration configuration = SessionConfiguration.getNewConfiguration();

        SessionFactory sessionFactory = new SessionFactory(setupFolder);
        Session session = sessionFactory.create(id, configuration);

        assertNotNull(session);
    }
}
