package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.api.game.Session;
import com.github.matsgemmeke.battlegrounds.api.game.SessionConfiguration;
import com.github.matsgemmeke.battlegrounds.game.DefaultSessionConfiguration;
import com.github.matsgemmeke.battlegrounds.game.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class SessionContextFactoryTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File dataFolder;

    @Before
    public void setUp() throws IOException {
        this.dataFolder = folder.newFolder("data");
    }

    @Test
    public void shouldBeAbleToMakeSessionInstance() {
        int sessionId = 1;

        SessionConfiguration configuration = DefaultSessionConfiguration.getNewConfiguration();
        SessionFactory sessionFactory = new SessionFactory(dataFolder);

        Session session = sessionFactory.make(sessionId, configuration);

        assertEquals(sessionId, session.getId());
    }
}
