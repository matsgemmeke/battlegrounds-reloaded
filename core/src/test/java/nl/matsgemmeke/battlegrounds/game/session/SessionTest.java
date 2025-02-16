package nl.matsgemmeke.battlegrounds.game.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SessionTest {

    private SessionConfiguration configuration;

    @BeforeEach
    public void setUp() {
        this.configuration = mock(SessionConfiguration.class);
    }

    @Test
    public void shouldBeAbleToGetConfiguration() {
        Session session = new Session(configuration);

        assertEquals(configuration, session.getConfiguration());
    }
}
