package nl.matsgemmeke.battlegrounds.game.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SessionTest {

    private int id;
    private SessionConfiguration configuration;

    @BeforeEach
    public void setUp() {
        this.configuration = mock(SessionConfiguration.class);
        this.id = 1;
    }

    @Test
    public void shouldBeAbleToGetId() {
        Session session = new Session(id, configuration);

        assertEquals(id, session.getId());
    }

    @Test
    public void shouldBeAbleToGetConfiguration() {
        Session session = new Session(id, configuration);

        assertEquals(configuration, session.getConfiguration());
    }
}
