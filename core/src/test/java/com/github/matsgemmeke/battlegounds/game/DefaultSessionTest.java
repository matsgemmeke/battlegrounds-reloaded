package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.game.SessionConfiguration;
import com.github.matsgemmeke.battlegrounds.game.DefaultSession;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultSessionTest {

    private int id;
    private InternalsProvider internals;
    private SessionConfiguration configuration;

    @Before
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
