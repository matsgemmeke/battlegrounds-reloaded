package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class DefaultSessionTest {

    private int id;
    private InternalsProvider internals;
    private ItemStorage<Gun, GunHolder> gunStorage;
    private SessionConfiguration configuration;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.internals = mock(InternalsProvider.class);
        this.gunStorage = (ItemStorage<Gun, GunHolder>) mock(ItemStorage.class);
        this.configuration = mock(SessionConfiguration.class);
        this.id = 1;
    }

    @Test
    public void shouldBeAbleToGetId() {
        DefaultSession session = new DefaultSession(id, configuration, internals, gunStorage);

        assertEquals(id, session.getId());
    }

    @Test
    public void shouldBeAbleToGetConfiguration() {
        DefaultSession session = new DefaultSession(id, configuration, internals, gunStorage);

        assertEquals(configuration, session.getConfiguration());
    }
}
