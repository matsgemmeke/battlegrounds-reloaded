package nl.matsgemmeke.battlegrounds.arena;

import com.google.inject.Binder;
import com.google.inject.Module;
import nl.matsgemmeke.battlegrounds.game.GameScoped;

public class ArenaModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(Arena.class).toProvider(ArenaProvider.class).in(GameScoped.class);
    }
}
