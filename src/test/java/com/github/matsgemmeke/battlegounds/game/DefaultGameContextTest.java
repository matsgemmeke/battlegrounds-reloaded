package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.game.GameConfiguration;
import com.github.matsgemmeke.battlegrounds.game.DefaultGameContext;
import com.github.matsgemmeke.battlegrounds.item.BlockCollisionChecker;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultGameContextTest {

    private BlockCollisionChecker collisionChecker;
    private GameConfiguration configuration;
    private int id;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.collisionChecker = mock(BlockCollisionChecker.class);
        this.configuration = mock(GameConfiguration.class);
        this.taskRunner = mock(TaskRunner.class);
        this.id = 1;
    }

    @Test
    public void shouldBeAbleToGetId() {
        DefaultGameContext context = new DefaultGameContext(collisionChecker, taskRunner, id, configuration);

        assertEquals(id, context.getId());
    }

    @Test
    public void shouldBeAbleToGetConfiguration() {
        DefaultGameContext context = new DefaultGameContext(collisionChecker, taskRunner, id, configuration);

        assertEquals(configuration, context.getConfiguration());
    }
}
