package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SessionContextFactoryTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File dataFolder;
    private InternalsProvider internals;

    @Before
    public void setUp() throws IOException {
        this.internals = mock(InternalsProvider.class);

        this.dataFolder = folder.newFolder("data");
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
