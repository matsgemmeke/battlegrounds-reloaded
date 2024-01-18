package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.api.game.SessionConfiguration;
import com.github.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import com.github.matsgemmeke.battlegrounds.game.DefaultSession;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultSessionContextTest {

    private BlockCollisionChecker collisionChecker;
    private int id;
    private SessionConfiguration configuration;

    @Before
    public void setUp() {
        this.collisionChecker = mock(BlockCollisionChecker.class);
        this.configuration = mock(SessionConfiguration.class);
        this.id = 1;
    }

    @Test
    public void shouldBeAbleToGetId() {
        DefaultSession session = new DefaultSession(collisionChecker, id, configuration);

        assertEquals(id, session.getId());
    }

    @Test
    public void shouldBeAbleToGetConfiguration() {
        DefaultSession session = new DefaultSession(collisionChecker, id, configuration);

        assertEquals(configuration, session.getConfiguration());
    }
}
