package nl.matsgemmeke.battlegrounds.game.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SessionContextFactoryTest {

    @TempDir
    private File dataFolder;

    @Test
    public void shouldBeAbleToMakeSessionInstance() {
        int sessionId = 1;

        SessionConfiguration configuration = SessionConfiguration.getNewConfiguration();
        SessionContextFactory sessionContextFactory = new SessionContextFactory(dataFolder);

        SessionContext context = sessionContextFactory.make(sessionId, configuration);

        assertNotNull(context);
    }
}
