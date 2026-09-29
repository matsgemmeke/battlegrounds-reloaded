package nl.matsgemmeke.battlegrounds.arena;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;
import nl.matsgemmeke.battlegrounds.arena.component.membership.ArenaMembershipService;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScoped;
import nl.matsgemmeke.battlegrounds.game.component.membership.MembershipService;

public class ArenaModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(Arena.class).toProvider(ArenaProvider.class).in(GameScoped.class);

        MapBinder<GameContextType, MembershipService> membershipServiceBinder = MapBinder.newMapBinder(binder, GameContextType.class, MembershipService.class);
        membershipServiceBinder.addBinding(GameContextType.ARENA_MODE).to(ArenaMembershipService.class);
    }
}
