package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class DefaultSessionTest {

    private int id;
    private InternalsProvider internals;
    private SessionConfiguration configuration;

    @BeforeEach
    public void setUp() {
        this.internals = mock(InternalsProvider.class);
        this.configuration = mock(SessionConfiguration.class);
        this.id = 1;
    }

    @Test
    public void shouldBeAbleToGetId() {
        DefaultSession session = new DefaultSession(id, configuration, internals);

        assertEquals(id, session.getId());
    }

    @Test
    public void shouldBeAbleToGetConfiguration() {
        DefaultSession session = new DefaultSession(id, configuration, internals);

        assertEquals(configuration, session.getConfiguration());
    }
}
