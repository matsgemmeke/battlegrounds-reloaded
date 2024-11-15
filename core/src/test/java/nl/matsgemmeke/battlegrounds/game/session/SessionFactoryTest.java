package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SessionFactoryTest {

    @TempDir
    private File dataFolder;
    private InternalsProvider internals;

    @BeforeEach
    public void setUp() throws IOException {
        this.internals = mock(InternalsProvider.class);
    }

    @Test
    public void shouldBeAbleToMakeSessionInstance() {
        int sessionId = 1;

        SessionConfiguration configuration = DefaultSessionConfiguration.getNewConfiguration();
        SessionFactory sessionFactory = new SessionFactory(dataFolder, internals);

        Session session = sessionFactory.make(sessionId, configuration);

        assertEquals(sessionId, session.getId());
    }
}
