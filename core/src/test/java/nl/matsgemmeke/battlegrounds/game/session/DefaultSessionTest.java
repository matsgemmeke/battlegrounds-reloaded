package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DefaultSessionTest {

    private int id;
    private InternalsProvider internals;
    private ItemRegister<Gun, GunHolder> gunRegister;
    private SessionConfiguration configuration;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        this.internals = mock(InternalsProvider.class);
        this.gunRegister = (ItemRegister<Gun, GunHolder>) mock(ItemRegister.class);
        this.configuration = mock(SessionConfiguration.class);
        this.id = 1;
    }

    @Test
    public void shouldBeAbleToGetId() {
        DefaultSession session = new DefaultSession(id, configuration, internals, gunRegister);

        assertEquals(id, session.getId());
    }

    @Test
    public void shouldBeAbleToGetConfiguration() {
        DefaultSession session = new DefaultSession(id, configuration, internals, gunRegister);

        assertEquals(configuration, session.getConfiguration());
    }
}
